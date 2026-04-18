package org.example.aifoundandlost.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 移除原有ResponseUtil，改用项目统一的Result返回类
 * 保留CrossOrigin解决跨域，接口路径不变
 * 异常全部交由GlobalExceptionHandler处理，控制器仅做入参校验和业务调用
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin // 如需全局跨域可移到配置类，这里临时保留
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 生成物品描述
     */
    @PostMapping("/generate-description")
    public Result<String> generateDescription(@RequestParam String itemName) {
        // 仅保留入参校验（提前拦截无效请求）
        if (!StringUtils.hasText(itemName)) {
            throw new BusinessException(400, "物品名称不能为空"); // 抛业务异常，交给全局处理器
        }
        //异常直接抛给全局处理器
        String description = aiService.generateDescription(itemName);
        return Result.success(description);
    }

    /**
     * 分析数据
     */
    @GetMapping("/analyze-data")
    public Result<String> analyzeData() {
        // 无入参，直接调用服务，异常抛给全局处理器
        String analysis = aiService.analyzeData();
        return Result.success(analysis);
    }

    /**
     * 搜索物品
     */
    @PostMapping("/search-items")
    public Result<String> searchItems(@RequestParam String description) {
        // 入参校验
        if (!StringUtils.hasText(description)) {
            throw new BusinessException(400, "物品描述不能为空");
        }

        String result = aiService.searchItems(description);
        return Result.success(result);
    }
}