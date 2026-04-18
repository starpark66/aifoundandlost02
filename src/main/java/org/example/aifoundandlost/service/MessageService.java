package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {

    // 发送消息（完整留存）
    boolean sendMessage(Message message);

    // 获取和某个用户的完整对话列表（按时间顺序分页，所有消息都保留）
    Page<Message> getChatHistory(Long userId, Long otherId, Integer current, Integer size);

    // 获取用户的所有对话用户列表（只取每个对话的最新一条消息，用于前端展示）
    List<Message> getConversationList(Long userId);
}