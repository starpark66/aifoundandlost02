package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.Comment;
import java.util.List;

public interface CommentService extends IService<Comment> {

    boolean addComment(Comment comment);

    // 返回树形结构
    List<Comment> listTree(Integer targetType, Long targetId);

    boolean deleteComment(Long cid, Long uid);
}