package org.example.aifoundandlost.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.AIService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate-description")
    public Result<String> generateDescription(@RequestParam String itemName) {
        if (!StringUtils.hasText(itemName)) {
            throw new BusinessException(400, "物品名称不能为空");
        }
        log.info("AI生成物品描述：{}", itemName);
        String description = aiService.generateDescription(itemName);
        return Result.success(description);
    }

    @GetMapping("/analyze-data")
    public Result<String> analyzeData() {
        log.info("AI 开始分析平台数据");
        String analysis = aiService.analyzeData();
        return Result.success(analysis);
    }

    @PostMapping("/search-items")
    public Result<String> searchItems(@RequestParam String description) {
        if (!StringUtils.hasText(description)) {
            throw new BusinessException(400, "描述内容不能为空");
        }
        log.info("AI根据描述搜索物品：{}", description);
        String result = aiService.searchItems(description);
        return Result.success(result);
    }
}