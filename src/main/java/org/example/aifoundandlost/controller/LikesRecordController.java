package org.example.aifoundandlost.controller;

import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.service.LikesRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikesRecordController {

    @Resource
    private LikesRecordService likesRecordService;

    @GetMapping("/isLiked")
    public Result<?> isLiked(Integer targetType, Long targetId, Long uid) {
        return Result.success(likesRecordService.isLiked(targetType, targetId, uid));
    }
}