package org.example.aifoundandlost.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.AIService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    private final ChatClient chatClient;

    public AIServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateDescription(String itemName) {
        if (!StringUtils.hasText(itemName)) {
            throw new BusinessException(400, "物品名称不能为空");
        }
        String prompt = "请为丢失或者捡到的物品【" + itemName + "】生成一段详细的描述，包括可能的特征、用途等，帮助识别物品。200字以内";
        log.debug("调用DeepSeek生成描述，物品：{}", itemName);
        return chatClient.call(prompt);
    }

    @Override
    public String analyzeData() {
        String prompt = "请分析校园失物招领平台：1.失物高发区域；2.高频丢失物品；3.防丢失建议。";
        log.debug("调用DeepSeek 分析平台数据");
        return chatClient.call(prompt);
    }

    @Override
    public String searchItems(String description) {
        if (!StringUtils.hasText(description)) {
            throw new BusinessException(400, "搜索描述不能为空");
        }
        String prompt = "根据描述匹配最可能的失物/拾物，按相关性排序：" + description;
        log.debug("调用DeepSeek搜索物品，描述：{}", description);
        return chatClient.call(prompt);
    }
}