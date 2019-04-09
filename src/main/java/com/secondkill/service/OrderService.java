package com.secondkill.service;

import com.secondkill.service.model.OrderModel;

public interface OrderService {

    //使用1.通过前端 urls上传过来秒杀活动Id,然后下单接口内校验对应Id是否对应于商品且活动已开始
    //2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
    OrderModel createOrder(Integer userId,Integer itemId,Integer promId,Integer amount) throws Exception;
}
