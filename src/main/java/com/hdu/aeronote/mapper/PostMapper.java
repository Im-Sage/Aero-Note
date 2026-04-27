package com.hdu.aeronote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdu.aeronote.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
