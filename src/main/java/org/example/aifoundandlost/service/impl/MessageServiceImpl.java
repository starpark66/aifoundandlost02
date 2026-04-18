package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.Message;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.mapper.MessageMapper;
import org.example.aifoundandlost.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessage(Message message) {
        if (message.getFromId() == null || message.getToId() == null) {
            throw new BusinessException(400, "发送方和接收方不能为空");
        }
        if (message.getFromId().equals(message.getToId())) {
            throw new BusinessException(400, "不能给自己发消息");
        }
        if (!StringUtils.hasText(message.getContent())) {
            throw new BusinessException(400, "消息内容不能为空");
        }
        return save(message);
    }

    @Override
    public Page<Message> getChatHistory(Long userId, Long otherId, Integer current, Integer size) {
        if (userId == null || otherId == null) {
            throw new BusinessException(400, "用户信息不能为空");
        }
        LambdaQueryWrapper<Message> wrapper = Wrappers.lambdaQuery();
        wrapper.and(w ->
                w.eq(Message::getFromId, userId).eq(Message::getToId, otherId)
        ).or(w ->
                w.eq(Message::getFromId, otherId).eq(Message::getToId, userId)
        );
        wrapper.orderByAsc(Message::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<Message> getConversationList(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(400, "用户ID不合法");
        }
        LambdaQueryWrapper<Message> wrapper = Wrappers.lambdaQuery();
        wrapper.and(w ->
                w.eq(Message::getFromId, userId).or().eq(Message::getToId, userId)
        );
        wrapper.orderByDesc(Message::getCreateTime);

        List<Message> allMessages = list(wrapper);
        Map<String, Message> map = new LinkedHashMap<>();

        for (Message msg : allMessages) {
            long a = msg.getFromId();
            long b = msg.getToId();
            String key = a < b ? a + "_" + b : b + "_" + a;

            if (!map.containsKey(key)) {
                map.put(key, msg);
            }
            if (map.size() >= 20) {
                break;
            }
        }
        return new ArrayList<>(map.values());
    }
}