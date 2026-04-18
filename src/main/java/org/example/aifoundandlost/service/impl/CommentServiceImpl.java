package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.Comment;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.mapper.CommentMapper;
import org.example.aifoundandlost.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addComment(Comment comment) {
        if (comment.getUid() == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (comment.getTargetType() == null || comment.getTargetId() == null) {
            throw new BusinessException(400, "评论目标不能为空");
        }
        if (!StringUtils.hasText(comment.getContent())) {
            throw new BusinessException(400, "评论内容不能为空");
        }
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        return save(comment);
    }

    @Override
    public List<Comment> listTree(Integer targetType, Long targetId) {
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Comment::getTargetType, targetType)
                .eq(Comment::getTargetId, targetId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> allComments = list(wrapper);

        Map<Long, List<Comment>> groupByParent = allComments.stream()
                .collect(Collectors.groupingBy(c ->
                        c.getParentId() == null ? 0L : c.getParentId()));

        for (Comment c : allComments) {
            List<Comment> children = groupByParent.get(c.getCid());
            if (children != null) {
                c.setChildren(children);
            }
        }

        return groupByParent.getOrDefault(0L, new ArrayList<>());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long cid, Long uid) {
        Comment comment = getById(cid);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (!comment.getUid().equals(uid)) {
            throw new BusinessException(403, "无权限删除该评论");
        }
        // 递归删除子评论
        deleteChildren(cid);
        return removeById(cid);
    }

    /**
     * 递归删除子评论（事务包裹，保证原子性）
     */
    private void deleteChildren(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Comment::getParentId, parentId);
        List<Comment> children = list(wrapper);
        for (Comment child : children) {
            deleteChildren(child.getCid());
            removeById(child.getCid());
        }
    }
}