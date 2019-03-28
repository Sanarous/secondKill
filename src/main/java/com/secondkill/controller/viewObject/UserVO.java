package com.secondkill.controller.viewObject;

import lombok.Data;

/**
 * 展现给前端的用户信息的字段设置
 */
@Data
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
}
