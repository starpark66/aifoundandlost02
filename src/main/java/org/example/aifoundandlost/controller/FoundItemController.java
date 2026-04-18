package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.FoundItem;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.FoundItemService;
import org.example.aifoundandlost.util.UserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/found")
public class FoundItemController {

    @Resource
    private FoundItemService foundItemService;

    @PostMapping("/publish")
    public Result<?> publish(@RequestBody FoundItem foundItem) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        foundItem.setUid(currentUid);
        foundItemService.publishFoundItem(foundItem);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size,
                          String area, String keyword, Integer status) {
        current = (current == null || current < 1) ? 1 : current;
        size = (size == null || size < 1) ? 10 : size;
        Page<FoundItem> page = foundItemService.pageList(current, size, area, keyword, status);
        return Result.success(page);
    }

    @GetMapping("/detail/{fid}")
    public Result<?> detail(@PathVariable Long fid) {
        if (fid == null || fid <= 0) {
            throw new BusinessException(400, "参数不合法");
        }
        return Result.success(foundItemService.getById(fid));
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody FoundItem foundItem) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (foundItem.getFid() == null) {
            throw new BusinessException(400, "拾获物品ID不能为空");
        }
        foundItemService.updateFoundItem(foundItem);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<?> delete(Long fid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (fid == null || fid <= 0) {
            throw new BusinessException(400, "参数不合法");
        }
        foundItemService.deleteFoundItem(fid, currentUid);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<?> status(Long fid, Integer status) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (fid == null || fid <= 0) {
            throw new BusinessException(400, "参数不合法");
        }
        if (status == null) {
            throw new BusinessException(400, "状态不能为空");
        }
        foundItemService.changeStatus(fid, status);
        return Result.success();
    }

    @PostMapping("/like")
    public Result<?> like(Long fid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (fid == null || fid <= 0) {
            throw new BusinessException(400, "参数不合法");
        }
        return Result.success(foundItemService.doLike(fid, currentUid));
    }

    @PostMapping("/cancelLike")
    public Result<?> cancelLike(Long fid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (fid == null || fid <= 0) {
            throw new BusinessException(400, "参数不合法");
        }
        return Result.success(foundItemService.cancelLike(fid, currentUid));
    }

    @GetMapping("/isLiked")
    public Result<?> isLiked(Long fid) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (fid == null || fid <= 0) {
            throw new BusinessException(400, "参数不合法");
        }
        return Result.success(foundItemService.isLiked(fid, currentUid));
    }
}