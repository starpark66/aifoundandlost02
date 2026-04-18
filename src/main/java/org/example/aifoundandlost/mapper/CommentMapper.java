package org.example.aifoundandlost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.aifoundandlost.entity.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}