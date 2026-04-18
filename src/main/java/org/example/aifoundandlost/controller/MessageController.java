package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.Message;
import org.example.aifoundandlost.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/send")
    public Result<?> send(@RequestBody Message message) {
        messageService.sendMessage(message);
        return Result.success();
    }

    @GetMapping("/history")
    public Result<?> chatHistory(Long userId, Long otherId,
                                 Integer current, Integer size) {
        Page<Message> page = messageService.getChatHistory(userId, otherId, current, size);
        return Result.success(page);
    }

    @GetMapping("/conversationList")
    public Result<?> conversationList(Long userId) {
        List<Message> list = messageService.getConversationList(userId);
        return Result.success(list);
    }
}