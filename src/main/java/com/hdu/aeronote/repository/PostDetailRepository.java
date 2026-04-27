package com.hdu.aeronote.repository;

import com.hdu.aeronote.entity.PostDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostDetailRepository extends MongoRepository<PostDetail,String> {
    // 后面我们要靠这个方法，根据 MySQL 的 id 查出 Mongo 的详情
    PostDetail findByPostId(Long postId);
}
