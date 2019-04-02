package com.secondkill.service;

import com.secondkill.error.BusinessException;
import com.secondkill.service.model.OrderModel;

public interface OrderService {

    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws Exception;
}
