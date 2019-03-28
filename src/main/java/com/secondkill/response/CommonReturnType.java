package com.secondkill.response;

import lombok.Data;

@Data
public class CommonReturnType {
    //表面对应请求的返回处理，有success或fail
    private String status;

    //若status=success，则data内返回前端需要的json数据
    //若status=fail，则data内使用通用的错误码格式
    private Object data;


    //定义一个通用的创建的方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
}
