package com.hdu.aeronote.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.Map;

/**
 * MongoDB 中的 PostDetail 实体类，用于存储笔记的详细信息，包括内容和摄影参数（EXIF 信息）。
 * 这个类的属性应该与 MongoDB 中存储的文档结构对应，以便于后端能够正确存储和查询这些数据。
 * 其中，exifInfo 是一个 Map，用于存储前端动态表单中提交的摄影参数（EXIF
 */
@Data
public class PostDetail {
    @Id
    private String id; // 这是 Mongo 自己生成的 _id (一串乱码)

    @Indexed(unique = true) // 极其重要：给 postId 加唯一索引，加速查询
    private Long postId; // 这个才是关联 MySQL t_post 表的纽带！

    private String richContent;
    private Map<String, Object> exifInfo; // 灵活的 JSON，照单全收

    List<String> tags;    // 提取3-5个核心标签
    String summary;    // 50字以内的摘要
    String sentiment;         // 情感倾向 (如：积极、消极、治愈、震撼)
}
