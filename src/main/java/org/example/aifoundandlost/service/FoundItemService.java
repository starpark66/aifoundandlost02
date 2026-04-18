package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.FoundItem;

public interface FoundItemService extends IService<FoundItem> {

    boolean publishFoundItem(FoundItem foundItem);

    Page<FoundItem> pageList(Integer current, Integer size, String area, String keyword, Integer status);

    boolean updateFoundItem(FoundItem foundItem);

    boolean deleteFoundItem(Long fid, Long uid);

    boolean changeStatus(Long fid, Integer status);

    boolean doLike(Long fid, Long uid);

    boolean cancelLike(Long fid, Long uid);

    boolean isLiked(Long fid, Long uid);
}