package org.example.aifoundandlost.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.aifoundandlost.entity.LostItem;

public interface LostItemService extends IService<LostItem> {

    boolean publishLostItem(LostItem lostItem);

    Page<LostItem> pageList(Integer current, Integer size, String area, String keyword, Integer status);

    boolean updateLostItem(LostItem lostItem);

    boolean deleteLostItem(Long lid, Long uid);

    boolean changeStatus(Long lid, Integer status);

    boolean doLike(Long lid, Long uid);

    boolean cancelLike(Long lid, Long uid);

    boolean isLiked(Long lid, Long uid);
}