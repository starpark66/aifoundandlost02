package org.example.aifoundandlost.service.impl;

import org.example.aifoundandlost.service.AIService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Frthinker
 */
@Service
public class AIServiceImpl implements AIService {

    // 优化：添加@Qualifier避免多ChatClient冲突（可选，当前只有一个Bean可省略）
    @Autowired
    private ChatClient chatClient;

    @Override
    public String generateDescription(String itemName) {
        String prompt = "请为丢失或者捡到的物品【" + itemName + "】生成一段详细的描述，包括可能的特征、用途等，帮助识别物品。字数在200字以内";
        return chatClient.call(prompt);
    }

    @Override
    public String analyzeData() {
        String prompt = "请分析校园失物招领平台的数据分析结果，包括：1. 哪块区域的失物较多；2. 哪种物品最近丢的比较多；3. 给出一些减少丢失物品的建议。";
        return chatClient.call(prompt);
    }

    @Override
    public String searchItems(String description) {
        String prompt = "根据以下描述，搜索最可能的失物或拾物：" + description + "。请按照相关性从大到小排序，返回可能的物品列表。";
        return chatClient.call(prompt);
    }
}