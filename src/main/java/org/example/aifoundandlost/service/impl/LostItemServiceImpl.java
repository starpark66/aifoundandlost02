package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.aifoundandlost.entity.LostItem;
import org.example.aifoundandlost.entity.LikesRecord;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.mapper.LostItemMapper;
import org.example.aifoundandlost.service.LostItemService;
import org.example.aifoundandlost.service.LikesRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LostItemServiceImpl extends ServiceImpl<LostItemMapper, LostItem> implements LostItemService {

    @Resource
    private LikesRecordService likesRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishLostItem(LostItem lostItem) {
        if (lostItem.getUid() == null) {
            throw new BusinessException(401, "用户未登录");
        }
        if (!StringUtils.hasText(lostItem.getName()) || !StringUtils.hasText(lostItem.getDescription())) {
            throw new BusinessException(400, "物品名称和描述不能为空");
        }
        if (lostItem.getLostTime() == null) {
            throw new BusinessException(400, "丢失时间不能为空");
        }
        lostItem.setStatus(0);
        lostItem.setLikes(0L);
        lostItem.setHot(0);
        return save(lostItem);
    }

    @Override
    public Page<LostItem> pageList(Integer current, Integer size, String area, String keyword, Integer status) {
        LambdaQueryWrapper<LostItem> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(area)) {
            wrapper.eq(LostItem::getArea, area);
        }
        if (status != null) {
            wrapper.eq(LostItem::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(LostItem::getName, keyword)
                    .or()
                    .like(LostItem::getDescription, keyword);
        }
        wrapper.orderByDesc(LostItem::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLostItem(LostItem lostItem) {
        LostItem dbItem = getById(lostItem.getLid());
        if (dbItem == null) {
            throw new BusinessException(404, "该失物不存在");
        }
        return updateById(lostItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLostItem(Long lid, Long uid) {
        LostItem item = getById(lid);
        if (item == null) {
            throw new BusinessException(404, "失物不存在");
        }
        if (!item.getUid().equals(uid)) {
            throw new BusinessException(403, "无权限删除该失物");
        }
        return removeById(lid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Long lid, Integer status) {
        LostItem item = getById(lid);
        if (item == null) {
            throw new BusinessException(404, "失物不存在");
        }
        return lambdaUpdate()
                .eq(LostItem::getLid, lid)
                .set(LostItem::getStatus, status)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doLike(Long lid, Long uid) {
        if (isLiked(lid, uid)) {
            throw new BusinessException(400, "已点赞过该失物");
        }
        LikesRecord record = new LikesRecord();
        record.setUid(uid);
        record.setTargetType(1);
        record.setTargetId(lid);
        likesRecordService.save(record);

        lambdaUpdate()
                .eq(LostItem::getLid, lid)
                .setSql("likes = likes + 1")
                .update();
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelLike(Long lid, Long uid) {
        boolean removed = likesRecordService.lambdaUpdate()
                .eq(LikesRecord::getUid, uid)
                .eq(LikesRecord::getTargetType, 1)
                .eq(LikesRecord::getTargetId, lid)
                .remove();

        if (removed) {
            lambdaUpdate()
                    .eq(LostItem::getLid, lid)
                    .setSql("likes = likes - 1")
                    .update();
        }else {
            throw new BusinessException(400, "未点赞该失物，无法取消");
        }
        return removed;
    }

    @Override
    public boolean isLiked(Long lid, Long uid) {
        return likesRecordService.lambdaQuery()
                .eq(LikesRecord::getUid, uid)
                .eq(LikesRecord::getTargetType, 1)
                .eq(LikesRecord::getTargetId, lid)
                .exists();
    }
}