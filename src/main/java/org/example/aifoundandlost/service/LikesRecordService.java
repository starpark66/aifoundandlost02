package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.LikesRecord;

public interface LikesRecordService extends IService<LikesRecord> {

    boolean isLiked(Integer targetType, Long targetId, Long uid);

    boolean removeLike(Integer targetType, Long targetId, Long uid);
}