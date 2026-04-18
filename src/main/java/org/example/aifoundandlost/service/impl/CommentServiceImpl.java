package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.Comment;
import org.example.aifoundandlost.mapper.CommentMapper;
import org.example.aifoundandlost.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Override
    public boolean addComment(Comment comment) {
        if (comment.getUid() == null) {
            throw new RuntimeException("请先登录");
        }
        if (comment.getTargetType() == null || comment.getTargetId() == null) {
            throw new RuntimeException("评论目标不能为空");
        }
        if (!StringUtils.hasText(comment.getContent())) {
            throw new RuntimeException("评论内容不能为空");
        }
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        return save(comment);
    }

    @Override
    public List<Comment> listTree(Integer targetType, Long targetId) {
        // 1. 查出所有评论（包含所有层级）
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Comment::getTargetType, targetType)
                .eq(Comment::getTargetId, targetId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> allComments = list(wrapper);

        // 2. 按 parentId 分组
        Map<Long, List<Comment>> groupByParent = allComments.stream()
                .collect(Collectors.groupingBy(c ->
                        c.getParentId() == null ? 0L : c.getParentId()));

        // 3. 给每个评论设置 children
        for (Comment c : allComments) {
            List<Comment> children = groupByParent.get(c.getCid());
            if (children != null) {
                c.setChildren(children);
            }
        }

        // 4. 只返回 parentId=0 的顶级评论（它们的 children 里已经挂好了所有子评论）
        return groupByParent.getOrDefault(0L, new ArrayList<>());
    }

    @Override
    public boolean deleteComment(Long cid, Long uid) {
        Comment comment = getById(cid);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUid().equals(uid)) {
            throw new RuntimeException("无权限删除该评论");
        }
        // 同时删除该评论的所有子评论（递归删除）
        deleteChildren(cid);
        return removeById(cid);
    }

    private void deleteChildren(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Comment::getParentId, parentId);
        List<Comment> children = list(wrapper);
        for (Comment child : children) {
            // 递归删除
            deleteChildren(child.getCid());
            removeById(child.getCid());
        }
    }
}