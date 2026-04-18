package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.Report;

public interface ReportService extends IService<Report> {

    boolean addReport(Report report);

    Page<Report> pageList(Integer current, Integer size, Integer status);

    boolean handleReport(Long rid, Integer status);
}