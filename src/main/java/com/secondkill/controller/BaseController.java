package com.secondkill.controller;

import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理错误处理，在Controller层做一层钩子处理掉所有可能的异常
 */
@Controller
public class BaseController {

    public static final String CONTEXT_TYPE_FORMED = "application/x-www-form-urlencoded";

    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData = new HashMap<>();

        if(ex instanceof BusinessException){
            BusinessException businessException = (BusinessException) ex;

            responseData.put("errCode",businessException.getErrMsg());
            responseData.put("errMsg",businessException.getErrMsg());
        }else{
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
