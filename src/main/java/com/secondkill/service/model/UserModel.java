package com.secondkill.service.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户领域模型
 */
@Data
public class UserModel {
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    private String name;

    private Integer age;
    @NotNull(message = "年龄不能为空")
    @Min(value = 0,message = "年龄必须大于0")
    @Max(value = 150,message = "年龄必须小于150岁")

    @NotNull(message = "性别不能为空")
    private Byte gender;

    @NotBlank(message = "手机号不能为空")
    private String telphone;
    private String registerMode;
    private Integer thirdPartyId;

    @NotBlank(message = "密码不能为空")
    private String encrptPassword;
}
