package com.secondkill.controller.viewObject;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemVO{
    private Integer id;

    //商品名称
    private String title;

    //商品价格
    private BigDecimal price;

    //商品库存
    private Integer stock;

    //商品的描述
    private String description;

    //商品的销量
    private Integer sales;

    //商品描述图片的url
    private String imgUrl;
}
