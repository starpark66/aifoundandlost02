package org.example.aifoundandlost.controller;

import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.LikesRecordService;
import org.example.aifoundandlost.util.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
public class LikesRecordController {

    @Resource
    private LikesRecordService likesRecordService;

    @GetMapping("/isLiked")
    public Result<?> isLiked(Integer targetType, Long targetId) {
        // 登录校验
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        // 参数合法性校验
        if (targetType == null) {
            throw new BusinessException(400, "目标类型不能为空");
        }
        if (targetId == null || targetId <= 0) {
            throw new BusinessException(400, "目标ID不合法");
        }
        return Result.success(likesRecordService.isLiked(targetType, targetId, currentUid));
    }
}