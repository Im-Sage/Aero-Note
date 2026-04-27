package com.hdu.aeronote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_post")
public class Post {
    // INPUT表示我们代码里手动生成并塞进去，而不是靠数据库自增
    private Long id;
    private Long userId;
    private String title;
    private String coverImageUrl;
    private Integer likeCount;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime createTime;
}
