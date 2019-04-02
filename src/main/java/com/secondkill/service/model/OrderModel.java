package com.secondkill.service.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户下单的交易模型
 */
@Data
public class OrderModel {
    //20181021151561451451
    private String id;

    //购买商品的单价
    private BigDecimal itemPrice;

    //用户id
    private Integer userId;

    //购买商品id
    private Integer itemId;

    //购买数量
    private Integer amount;

    //购买金额
    private BigDecimal orderPrice;
}
