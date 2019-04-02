package com.secondkill.validator;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class ValidationResult {
    //校验结果是否有错
    private  boolean hasErrors = false;

    //存放错误信息的map
    private Map<String,String> errMsgMap = new HashMap<>();

    //实现通用的通过格式化字符串信息获取错误结果的msg方法
    public String getErrMsg(){
        return StringUtils.join(errMsgMap.values().toArray(), ",");
    }
}
