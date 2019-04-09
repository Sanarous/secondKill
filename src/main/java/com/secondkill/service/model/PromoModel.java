package com.secondkill.service.model;

import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Data
public class PromoModel {

    private Integer id;

    //秒杀活动状态  1-未开始 2-进行中  3-已结束
    private Integer status;

    //秒杀活动名称
    private String promoName;

    //秒杀获得开始的时间
    private DateTime startDate;

    //秒杀活动的结束时间
    private DateTime endDate;

    //秒杀活动的适用商品
    private Integer itemId;

    //秒杀活动的商品价格
    private BigDecimal promoItemPrice;
}
