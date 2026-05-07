package com.hdu.aeronote.controller;

import com.hdu.aeronote.common.Result;
import com.hdu.aeronote.entity.dto.PostPublishDTO;
import com.hdu.aeronote.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/post")
public class PostController {
    @Autowired
    private PostService postService;
    @PostMapping("/publish")
    public Result<Long> publish(PostPublishDTO dto) {
        try {
            Long currentUserId = 10086L;
            Long postId = postService.publish(dto,currentUserId);
            return Result.success(postId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
