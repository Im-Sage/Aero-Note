package com.hdu.aeronote.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hdu.aeronote.dto.PostPublishDTO;
import com.hdu.aeronote.entity.Post;
import com.hdu.aeronote.entity.PostDetail;
import com.hdu.aeronote.mapper.PostMapper;
import com.hdu.aeronote.repository.PostDetailRepository;
import com.hdu.aeronote.service.PostService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private PostMapper postMapper; // Mysql
    @Autowired
    private PostDetailRepository postDetailRepository; // MongoDB
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
        return postId;
    }

}
