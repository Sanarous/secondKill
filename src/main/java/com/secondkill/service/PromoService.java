package com.secondkill.service;

import com.secondkill.service.model.PromoModel;

public interface PromoService {

    //根据商品id获取即将进行或者正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);
}
