package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.LostItem;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.LostItemService;
import org.example.aifoundandlost.util.UserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lost")
public class LostItemController {

    @Resource
    private LostItemService lostItemService;

    @PostMapping("/publish")
    public Result<?> publish(@RequestBody LostItem lostItem) {
        // 从上下文取当前登录用户，禁止前端传假uid
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        lostItem.setUid(currentUid);
        lostItemService.publishLostItem(lostItem);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size,
                          String area, String keyword, Integer status) {
        // 分页参数兜底（和UserController一致）
        current = (current == null || current < 1) ? 1 : current;
        size = (size == null || size < 1) ? 10 : size;
        Page<LostItem> page = lostItemService.pageList(current, size, area, keyword, status);
        return Result.success(page);
    }

    @GetMapping("/detail/{lid}")
    public Result<?> detail(@PathVariable Long lid) {
        // 参数合法性校验
        if (lid == null || lid <= 0) {
            throw new BusinessException(400, "失物ID不合法");
        }
        return Result.success(lostItemService.getById(lid));
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody LostItem lostItem) {
        // 登录校验
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        // 参数非空校验
        if (lostItem.getLid() == null) {
            throw new BusinessException(400, "失物ID不能为空");
        }
        lostItemService.updateLostItem(lostItem);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<?> delete(Long lid) {
        // 登录校验+参数校验
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (lid == null || lid <= 0) {
            throw new BusinessException(400, "失物ID不合法");
        }
        // 用上下文uid，无需前端传参，防越权
        lostItemService.deleteLostItem(lid, currentUid);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<?> status(Long lid, Integer status) {
        // 登录+参数校验
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (lid == null || lid <= 0) {
            throw new BusinessException(400, "失物ID不合法");
        }
        if (status == null) {
            throw new BusinessException(400, "状态不能为空");
        }
        lostItemService.changeStatus(lid, status);
        return Result.success();
    }

    @PostMapping("/like")
    public Result<?> like(Long lid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (lid == null || lid <= 0) {
            throw new BusinessException(400, "失物ID不合法");
        }
        return Result.success(lostItemService.doLike(lid, currentUid));
    }

    @PostMapping("/cancelLike")
    public Result<?> cancelLike(Long lid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (lid == null || lid <= 0) {
            throw new BusinessException(400, "失物ID不合法");
        }
        return Result.success(lostItemService.cancelLike(lid, currentUid));
    }

    @GetMapping("/isLiked")
    public Result<?> isLiked(Long lid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (lid == null || lid <= 0) {
            throw new BusinessException(400, "失物ID不合法");
        }
        return Result.success(lostItemService.isLiked(lid, currentUid));
    }
}