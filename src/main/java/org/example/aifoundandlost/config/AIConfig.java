package org.example.aifoundandlost.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient() {
        // 保持原有配置（deepseek接口+密钥）
        OpenAiApi openAiApi = new OpenAiApi("https://api.deepseek.com",
                "sk-c0d9d08cc0194dc892e78d24d43efaa4");
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel("deepseek-chat")
                .build();
        return new OpenAiChatClient(openAiApi, options);
    }
}