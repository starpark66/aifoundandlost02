package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.LikesRecord;
import org.example.aifoundandlost.exception.BusinessException;
import org.example.aifoundandlost.mapper.LikesRecordMapper;
import org.example.aifoundandlost.service.LikesRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikesRecordServiceImpl extends ServiceImpl<LikesRecordMapper, LikesRecord>
        implements LikesRecordService {

    @Override
    public boolean isLiked(Integer targetType, Long targetId, Long uid) {
        // 参数合法性校验
        if (targetType == null || targetId == null || uid == null) {
            throw new BusinessException(400, "参数不能为空");
        }
        if (targetId <= 0 || uid <= 0) {
            throw new BusinessException(400, "ID不合法");
        }
        return lambdaQuery()
                .eq(LikesRecord::getTargetType, targetType)
                .eq(LikesRecord::getTargetId, targetId)
                .eq(LikesRecord::getUid, uid)
                .exists();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeLike(Integer targetType, Long targetId, Long uid) {
        // 参数合法性校验
        if (targetType == null || targetId == null || uid == null) {
            throw new BusinessException(400, "参数不能为空");
        }
        if (targetId <= 0 || uid <= 0) {
            throw new BusinessException(400, "ID不合法");
        }
        // 校验是否存在点赞记录
        boolean isLiked = isLiked(targetType, targetId, uid);
        if (!isLiked) {
            throw new BusinessException(400, "未找到点赞记录");
        }
        return lambdaUpdate()
                .eq(LikesRecord::getTargetType, targetType)
                .eq(LikesRecord::getTargetId, targetId)
                .eq(LikesRecord::getUid, uid)
                .remove();
    }
}