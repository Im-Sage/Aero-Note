package com.hdu.aeronote.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 发布笔记的 DTO（数据传输对象）
 * 用于接收前端发布笔记时提交的数据，包括标题、内容、封面图片以及摄影参数（EXIF 信息）。
 * 这个类的属性应该与前端发布笔记表单中的字段对应，以便于后端能够正确接收和处理这些数据。
 * 其中，exifInfo 是一个 Map，用于存储前端动态表单中提交的摄影参数（EXIF 信息），键是参数名称，值是参数值
 */
@Data
public class PostPublishDTO {
    private String title;           // 标题
    private String content;         // 富文本内容
    private MultipartFile file;     // 封面图片文件

    // 摄影参数 (EXIF) - 对应前端动态表单
    private Map<String, Object> exifInfo;
}
