package com.secondkill.controller;

import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.response.CommonReturnType;
import com.secondkill.service.OrderService;
import com.secondkill.service.model.OrderModel;
import com.secondkill.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
@CrossOrigin(allowedHeaders = "*",allowCredentials = "true")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 下单Controller
     * @param itemId   下单的商品id
     * @param amount   下单的商品数量
     * @return   通用返回类型
     * @throws BusinessException  自定义异常
     */
    @PostMapping(value = "/createorder",consumes = {CONTEXT_TYPE_FORMED})
    public CommonReturnType createOrder(@RequestParam("itemId") Integer itemId,
                                        @RequestParam("amount") Integer amount,
                                        @RequestParam(value = "promId",required = false) Integer promId) throws Exception {
        //获取用户的登陆信息
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promId, amount);

        return CommonReturnType.create(null);
    }
}
