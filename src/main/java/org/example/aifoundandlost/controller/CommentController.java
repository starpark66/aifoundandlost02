package org.example.aifoundandlost.controller;

import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.Comment;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.CommentService;
import org.example.aifoundandlost.util.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody Comment comment) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        comment.setUid(currentUid);
        commentService.addComment(comment);
        return Result.success();
    }

    @GetMapping("/tree")
    public Result<?> tree(Integer targetType, Long targetId) {
        if (targetType == null) {
            throw new BusinessException(400, "目标类型不能为空");
        }
        if (targetId == null || targetId <= 0) {
            throw new BusinessException(400, "目标ID不合法");
        }
        List<Comment> tree = commentService.listTree(targetType, targetId);
        return Result.success(tree);
    }

    @PostMapping("/delete")
    public Result<?> delete(Long cid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (cid == null || cid <= 0) {
            throw new BusinessException(400, "评论ID不合法");
        }
        commentService.deleteComment(cid, currentUid);
        return Result.success();
    }
}