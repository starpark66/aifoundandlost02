package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.LostItem;
import org.example.aifoundandlost.service.LostItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lost")
public class LostItemController {

    @Resource
    private LostItemService lostItemService;

    @PostMapping("/publish")
    public Result<?> publish(@RequestBody LostItem lostItem) {
        lostItemService.publishLostItem(lostItem);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size,
                          String area, String keyword, Integer status) {
        Page<LostItem> page = lostItemService.pageList(current, size, area, keyword, status);
        return Result.success(page);
    }

    @GetMapping("/detail/{lid}")
    public Result<?> detail(@PathVariable Long lid) {
        return Result.success(lostItemService.getById(lid));
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody LostItem lostItem) {
        lostItemService.updateLostItem(lostItem);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<?> delete(Long lid, Long uid) {
        lostItemService.deleteLostItem(lid, uid);
        return Result.success();
    }

    @PostMapping("/status")
    public Result<?> status(Long lid, Integer status) {
        lostItemService.changeStatus(lid, status);
        return Result.success();
    }

    @PostMapping("/like")
    public Result<?> like(Long lid, Long uid) {
        return Result.success(lostItemService.doLike(lid, uid));
    }

    @PostMapping("/cancelLike")
    public Result<?> cancelLike(Long lid, Long uid) {
        return Result.success(lostItemService.cancelLike(lid, uid));
    }

    @GetMapping("/isLiked")
    public Result<?> isLiked(Long lid, Long uid) {
        return Result.success(lostItemService.isLiked(lid, uid));
    }
}