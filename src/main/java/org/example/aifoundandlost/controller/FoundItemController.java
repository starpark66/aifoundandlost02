package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.FoundItem;
import org.example.aifoundandlost.service.FoundItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/found")
public class FoundItemController {

    @Resource
    private FoundItemService foundItemService;

    @PostMapping("/publish")
    public Result<?> publish(@RequestBody FoundItem foundItem) {
        foundItemService.publishFoundItem(foundItem);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size,
                          String area, String keyword, Integer status) {
        Page<FoundItem> page = foundItemService.pageList(current, size, area, keyword, status);
        return Result.success(page);
    }

    @GetMapping("/detail/{fid}")
    public Result<?> detail(@PathVariable Long fid) {
        return Result.success(foundItemService.getById(fid));
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody FoundItem foundItem) {
        foundItemService.updateFoundItem(foundItem);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<?> delete(Long fid, Long uid) {
        foundItemService.deleteFoundItem(fid, uid);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<?> status(Long fid, Integer status) {
        foundItemService.changeStatus(fid, status);
        return Result.success();
    }

    @PostMapping("/like")
    public Result<?> like(Long fid, Long uid) {
        return Result.success(foundItemService.doLike(fid, uid));
    }

    @PostMapping("/cancelLike")
    public Result<?> cancelLike(Long fid, Long uid) {
        return Result.success(foundItemService.cancelLike(fid, uid));
    }

    @GetMapping("/isLiked")
    public Result<?> isLiked(Long fid, Long uid) {
        return Result.success(foundItemService.isLiked(fid, uid));
    }
}