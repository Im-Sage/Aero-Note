package com.hdu.aeronote.controllrt;

import com.hdu.aeronote.dto.PostPublishDTO;
import com.hdu.aeronote.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/post")
public class PostController {
    @Autowired
    private PostService postService;
    @PostMapping("/publish")
    public String publish(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("file") MultipartFile file
            // 真实开发中，EXIF 参数一般是前端解析好以 JSON 字符串传过来，这里为了极简演示先略过
    ) throws Exception {

        PostPublishDTO dto = new PostPublishDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setFile(file);

        // 模拟当前登录的用户 ID 为 10086
        Long currentUserId = 10086L;

        // 调用你刚才写的牛逼 Service
        Long postId = postService.publish(dto, currentUserId);

        return "✅ 发布成功！全局唯一帖子ID: " + postId;
    }
}
