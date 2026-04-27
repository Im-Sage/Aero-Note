package com.hdu.aeronote.service;

import com.hdu.aeronote.dto.PostPublishDTO;

public interface PostService {
    /**
     * 发布笔记 这个方法的实现需要完成以下几个步骤：
     * 1. 接收前端传来的 PostPublishDTO 对象，其中包含了笔记的标题、内容、封面图片以及摄影参数（EXIF 信息）。
     * 2. 将 PostPublishDTO 中的数据转换为 Post 实体对象，并保存到 MySQL 数据库中。保存后会得到一个新的 Post 对象，其中包含了 MySQL 生成的 id。
     * 3. 创建一个新的 PostDetail 对象，将 PostPublishDTO 中的内容和 EXIF 信息保存到这个对象中，并将 MySQL 生成的 id 赋值给 PostDetail 的 postId 字段。
     * 4. 将 PostDetail 对象保存到 MongoDB 数据库中。
     * @param dto
     * @param userId
     * @return
     * @throws Exception
     */
    Long publish(PostPublishDTO dto, Long userId) throws Exception;
}
