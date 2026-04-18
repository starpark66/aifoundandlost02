package org.example.aifoundandlost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.common.Result;
import org.example.aifoundandlost.entity.Message;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.service.MessageService;
import org.example.aifoundandlost.util.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/send")
    public Result<?> send(@RequestBody Message message) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        // 强制设置发送方为当前登录用户，防止前端伪造身份
        message.setFromId(currentUid);
        messageService.sendMessage(message);
        return Result.success();
    }

    @GetMapping("/history")
    public Result<?> chatHistory(Long otherId, Integer current, Integer size) {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (otherId == null || otherId <= 0) {
            throw new BusinessException(400, "聊天对象ID不合法");
        }
        current = (current == null || current < 1) ? 1 : current;
        size = (size == null || size < 1) ? 10 : size;
        Page<Message> page = messageService.getChatHistory(currentUid, otherId, current, size);
        return Result.success(page);
    }

    @GetMapping("/conversationList")
    public Result<?> conversationList() {
        Long currentUid = UserContext.get();
        if (currentUid == null) {
            throw new BusinessException(401, "请先登录");
        }
        List<Message> list = messageService.getConversationList(currentUid);
        return Result.success(list);
    }
}