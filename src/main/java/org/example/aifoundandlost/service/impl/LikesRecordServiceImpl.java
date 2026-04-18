package org.example.aifoundandlost.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.aifoundandlost.entity.LikesRecord;
import org.example.aifoundandlost.mapper.LikesRecordMapper;
import org.example.aifoundandlost.service.LikesRecordService;
import org.springframework.stereotype.Service;

@Service
public class LikesRecordServiceImpl extends ServiceImpl<LikesRecordMapper, LikesRecord>
        implements LikesRecordService {

    @Override
    public boolean isLiked(Integer targetType, Long targetId, Long uid) {
        return lambdaQuery()
                .eq(LikesRecord::getTargetType, targetType)
                .eq(LikesRecord::getTargetId, targetId)
                .eq(LikesRecord::getUid, uid)
                .exists();
    }

    @Override
    public boolean removeLike(Integer targetType, Long targetId, Long uid) {
        return lambdaUpdate()
                .eq(LikesRecord::getTargetType, targetType)
                .eq(LikesRecord::getTargetId, targetId)
                .eq(LikesRecord::getUid, uid)
                .remove();
    }
}