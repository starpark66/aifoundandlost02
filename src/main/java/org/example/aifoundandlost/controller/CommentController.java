package org.example.aifoundandlost.controller;

import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.Comment;
import org.example.aifoundandlost.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return Result.success();
    }

    @GetMapping("/tree")
    public Result<?> tree(Integer targetType, Long targetId) {
        List<Comment> tree = commentService.listTree(targetType, targetId);
        return Result.success(tree);
    }

    @PostMapping("/delete")
    public Result<?> delete(Long cid, Long uid) {
        commentService.deleteComment(cid, uid);
        return Result.success();
    }
}