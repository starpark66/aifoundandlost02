package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.Message;
import org.example.aifoundandlost.mapper.MessageMapper;
import org.example.aifoundandlost.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    // 发送消息：全部保存，无任何限制
    @Override
    public boolean sendMessage(Message message) {
        if (message.getFromId() == null || message.getToId() == null) {
            throw new RuntimeException("发送方和接收方不能为空");
        }
        if (message.getFromId().equals(message.getToId())) {
            throw new RuntimeException("不能给自己发消息");
        }
        if (!StringUtils.hasText(message.getContent())) {
            throw new RuntimeException("消息内容不能为空");
        }
        // 每条消息都完整入库，永久留存
        return save(message);
    }

    // 获取两人聊天历史：全部返回，无20条限制
    @Override
    public Page<Message> getChatHistory(Long userId, Long otherId, Integer current, Integer size) {
        LambdaQueryWrapper<Message> wrapper = Wrappers.lambdaQuery();
        wrapper.and(w ->
                w.eq(Message::getFromId, userId).eq(Message::getToId, otherId)
        ).or(w ->
                w.eq(Message::getFromId, otherId).eq(Message::getToId, userId)
        );
        wrapper.orderByAsc(Message::getCreateTime);

        // 消息无任何上限，分页全量返回
        return page(new Page<>(current, size), wrapper);
    }

    // 会话列表：只展示最新20个聊天对象
    // 消息本身不受任何影响，全部还在
    @Override
    public List<Message> getConversationList(Long userId) {
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

            // 只限制会话列表显示20个，不碰消息
            if (map.size() >= 20) {
                break;
            }
        }

        return new ArrayList<>(map.values());
    }
}