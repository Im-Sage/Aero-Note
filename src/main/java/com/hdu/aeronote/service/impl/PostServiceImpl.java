package com.hdu.aeronote.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hdu.aeronote.config.RabbitMqConfig;
import com.hdu.aeronote.entity.dto.PostPublishDTO;
import com.hdu.aeronote.entity.Post;
import com.hdu.aeronote.entity.PostDetail;
import com.hdu.aeronote.mapper.PostMapper;
import com.hdu.aeronote.mapper.UserMapper;
import com.hdu.aeronote.repository.PostDetailRepository;
import com.hdu.aeronote.service.PostService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    private static final int BIG_V_THRESHOLD = 100000; // 大 V 的粉丝数阈值，超过这个数就算大 V

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private PostMapper postMapper; // Mysql
    @Autowired
    private PostDetailRepository postDetailRepository; // MongoDB
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserMapper userMapper;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    @Transactional(rollbackFor = Exception.class) // 保证 MySQL 事务
    public Long publish(PostPublishDTO dto, Long userId) throws Exception {
        // 1. 生成全局唯一 ID
        long postId = IdWorker.getId();

        // 2.上传图片到MinIO
        String fileName = postId + ".jpg"; // 以 postId 作为文件名，确保唯一
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(dto.getFile().getInputStream(), dto.getFile().getSize(), -1)
                        .contentType(dto.getFile().getContentType())
                        .build()
        );
        String imageUrl = "http://localhost:9000/" + bucketName + "/" + fileName;
        // 3 存储“骨架到MySQL
        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setCoverImageUrl(imageUrl);
        post.setStatus(1);
        postMapper.insert(post);
        // 4 存储血肉到MongoDB
        PostDetail detail = new PostDetail();
        detail.setPostId(postId);
        detail.setRichContent(dto.getContent());
        // detail.setExifInfo(dto.getExifInfo()); // 暂时注销，等后续对接真实参数
        postDetailRepository.save(detail);
        log.info("🚀 笔记发布成功！ID: {}, 存储路径: MySQL & MongoDB", postId);

        // 5. 如果是大 V，发送消息到 RabbitMQ 进行后续处理（比如推送通知）
        // 查询当前发布者的粉丝数
        int followerCount = userMapper.getFollowerCount(userId);

        // 构建MQ消息体
        Map<String,Object> message = new HashMap<>();
        message.put("postId", postId);
        message.put("userId", userId);
        message.put("ts", System.currentTimeMillis());

        // 架构路由判定
        if (followerCount > BIG_V_THRESHOLD){
            // 如果是大 V，发送到专门的大 V 路由，后续可以有专门的消费者来处理大 V 的笔记（比如优先推送给粉丝）
             rabbitTemplate.convertAndSend(RabbitMqConfig.FEED_EXCHANGE, RabbitMqConfig.ROUTE_BIGV, message);
        }
        else {
            // 普通用户的笔记，发送到普通路由，后续可以有消费者来处理普通用户的笔记（比如正常推送给粉丝）
            rabbitTemplate.convertAndSend(RabbitMqConfig.FEED_EXCHANGE, RabbitMqConfig.ROUTE_NORMAL, message);
        }
        rabbitTemplate.convertAndSend(RabbitMqConfig.FEED_EXCHANGE, "route.ai", message);
        return postId;
    }

}
