package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.entity.FoundItem;
import org.example.aifoundandlost.entity.LikesRecord;
import org.example.aifoundandlost.mapper.FoundItemMapper;
import org.example.aifoundandlost.service.FoundItemService;
import org.example.aifoundandlost.service.LikesRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FoundItemServiceImpl extends ServiceImpl<FoundItemMapper, FoundItem>
        implements FoundItemService {

    @Resource
    private LikesRecordService likesRecordService;

    @Override
    public boolean publishFoundItem(FoundItem foundItem) {
        if (foundItem.getUid() == null) {
            throw new RuntimeException("用户未登录");
        }
        if (!StringUtils.hasText(foundItem.getName()) || !StringUtils.hasText(foundItem.getDescription())) {
            throw new RuntimeException("物品名称和描述不能为空");
        }
        if (foundItem.getFoundTime() == null) {
            throw new RuntimeException("拾获时间不能为空");
        }

        foundItem.setStatus(0);
        foundItem.setLikes(0L);
        foundItem.setHot(0);
        return save(foundItem);
    }

    @Override
    public Page<FoundItem> pageList(Integer current, Integer size, String area, String keyword, Integer status) {
        LambdaQueryWrapper<FoundItem> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(area)) {
            wrapper.eq(FoundItem::getArea, area);
        }
        if (status != null) {
            wrapper.eq(FoundItem::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(FoundItem::getName, keyword)
                    .or()
                    .like(FoundItem::getDescription, keyword);
        }
        wrapper.orderByDesc(FoundItem::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public boolean updateFoundItem(FoundItem foundItem) {
        FoundItem dbItem = getById(foundItem.getFid());
        if (dbItem == null) {
            throw new RuntimeException("该拾获物品不存在");
        }
        return updateById(foundItem);
    }

    @Override
    public boolean deleteFoundItem(Long fid, Long uid) {
        FoundItem item = getById(fid);
        if (item == null) {
            throw new RuntimeException("记录不存在");
        }
        if (!item.getUid().equals(uid)) {
            throw new RuntimeException("无权限删除");
        }
        return removeById(fid);
    }

    @Override
    public boolean changeStatus(Long fid, Integer status) {
        return lambdaUpdate()
                .eq(FoundItem::getFid, fid)
                .set(FoundItem::getStatus, status)
                .update();
    }

    @Override
    public boolean doLike(Long fid, Long uid) {
        if (isLiked(fid, uid)) {
            return false;
        }
        LikesRecord record = new LikesRecord();
        record.setUid(uid);
        record.setTargetType(2);
        record.setTargetId(fid);
        likesRecordService.save(record);

        lambdaUpdate()
                .eq(FoundItem::getFid, fid)
                .setSql("likes = likes + 1")
                .update();
        return true;
    }

    @Override
    public boolean cancelLike(Long fid, Long uid) {
        boolean removed = likesRecordService.removeLike(2, fid, uid);
        if (removed) {
            lambdaUpdate()
                    .eq(FoundItem::getFid, fid)
                    .setSql("likes = likes - 1")
                    .update();
        }
        return removed;
    }

    @Override
    public boolean isLiked(Long fid, Long uid) {
        return likesRecordService.isLiked(2, fid, uid);
    }
}