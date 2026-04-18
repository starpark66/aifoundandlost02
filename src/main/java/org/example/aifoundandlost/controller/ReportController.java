package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.Report;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.ReportService;
import org.example.aifoundandlost.util.UserContext;
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
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        // 强制设置举报人ID，防止前端伪造身份
        report.setReportId(currentUid);
        reportService.addReport(report);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> page(Integer current, Integer size, Integer status) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        // 分页参数兜底
        current = (current == null || current < 1) ? 1 : current;
        size = (size == null || size < 1) ? 10 : size;
        Page<Report> page = reportService.pageList(current, size, status);
        return Result.success(page);
    }

    @PostMapping("/handle")
    public Result<?> handle(Long rid, Integer status) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (rid == null || rid <= 0) {
            throw new BusinessException(400, "举报ID不合法");
        }
        if (status == null) {
            throw new BusinessException(400, "处理状态不能为空");
        }
        reportService.handleReport(rid, status);
        return Result.success();
    }
}