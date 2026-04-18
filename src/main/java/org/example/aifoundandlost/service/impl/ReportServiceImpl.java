package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.Report;
import org.example.aifoundandlost.mapper.ReportMapper;
import org.example.aifoundandlost.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Override
    public boolean addReport(Report report) {
        // 校验1：用户必须登录
        if (report.getReportId() == null) {
            throw new RuntimeException("请先登录");
        }
        // 校验2：举报目标不能为空
        if (report.getTargetType() == null || report.getTargetId() == null) {
            throw new RuntimeException("举报目标不能为空");
        }
        // 校验3：必须选择举报原因 （因为是选项，只判断是否为null）
        if (report.getReason() == null) {
            throw new RuntimeException("请选择举报原因");
        }
        // 校验4：限制原因值在合法选项内（可选，防止非法值）
        if (report.getReason() < 1 || report.getReason() > 5) {
            throw new RuntimeException("举报原因选项不合法");
        }

        // 初始状态为待处理
        report.setStatus(0);
        return save(report);
    }

    @Override
    public Page<Report> pageList(Integer current, Integer size, Integer status) {
        LambdaQueryWrapper<Report> wrapper = Wrappers.lambdaQuery();
        if (status != null) {
            wrapper.eq(Report::getStatus, status);
        }
        wrapper.orderByDesc(Report::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public boolean handleReport(Long rid, Integer status) {
        return lambdaUpdate()
                .eq(Report::getRid, rid)
                .set(Report::getStatus, status)
                .update();
    }
}