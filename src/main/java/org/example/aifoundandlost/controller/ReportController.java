package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.Report;
import org.example.aifoundandlost.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody Report report) {
        reportService.addReport(report);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size, Integer status) {
        Page<Report> page = reportService.pageList(current, size, status);
        return Result.success(page);
    }

    @PostMapping("/handle")
    public Result<?> handle(Long rid, Integer status) {
        reportService.handleReport(rid, status);
        return Result.success();
    }
}