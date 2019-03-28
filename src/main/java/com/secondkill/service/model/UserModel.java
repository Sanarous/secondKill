package com.secondkill.service.model;

import lombok.Data;

/**
 * 用户领域模型
 */
@Data
public class UserModel {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
    private String registerMode;
    private Integer thirdPartyId;

    private String encrptPassword;
}
