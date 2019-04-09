package com.secondkill.service.UserServiceImpl;

import com.secondkill.dao.OrderInfoMapper;
import com.secondkill.dao.SequenceInfoMapper;
import com.secondkill.dataobject.OrderInfo;
import com.secondkill.dataobject.SequenceInfo;
import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.service.ItemService;
import com.secondkill.service.OrderService;
import com.secondkill.service.UserService;
import com.secondkill.service.model.ItemModel;
import com.secondkill.service.model.OrderModel;
import com.secondkill.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单业务逻辑
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promId,Integer amount) throws Exception {
        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }

        //校验用户是否存在
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户不存在");
        }
        //校验购买商品数量
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不正确");
        }
        //校验活动信息
        if(promId != null){
            //校验对应活动是否存在这个适用商品
            if(promId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
                //校验活动是否正在进行中
            }else if(itemModel.getPromoModel().getStatus().intValue() != 2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息还未开始");
            }
        }

        //2.落单减库存（也可以支付减库存）
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setPromId(promId);
        orderModel.setAmount(amount);
        if(promId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号
        String id = generateOrderNo();
        orderModel.setId(id);

        OrderInfo orderInfo = convertFromOrderModel(orderModel);
        orderInfoMapper.insertSelective(orderInfo);

        //加上商品的销量
        itemService.increaseSales(itemId, amount);

        //4.返回前端
        return orderModel;
    }

    /**
     * 产生全局唯一的订单号
     * @return  生成的订单号
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)   //设置传播行为，这样即使上面产生订单的事务失败，这个也不会崩溃，从而保证订单号的全局唯一性
    public String generateOrderNo() {
        StringBuilder sb = new StringBuilder();
        //订单号有16位
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        sb.append(nowDate);

        //中间6位为自增序列
        //获取当前的sequence
        int sequence = 0;
        SequenceInfo sequenceInfo = sequenceInfoMapper.getSequenceByName("order_info");

        sequence = sequenceInfo.getCurrentValue();
        sequenceInfo.setCurrentValue(sequenceInfo.getCurrentValue() + sequenceInfo.getStep());
        sequenceInfoMapper.updateByPrimaryKeySelective(sequenceInfo);

        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            sb.append(0);
        }
        sb.append(sequenceStr);

        //最后2位为分库分表位,暂时写死
        sb.append("00");

        return sb.toString();
    }

    /**
     * 从订单领域模型转换成订单实体类
     * @param orderModel  订单的领域模型
     * @return  订单实体类
     */
    private OrderInfo convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null) return null;
        OrderInfo order = new OrderInfo();
        BeanUtils.copyProperties(orderModel, order);
        order.setItemPrice(orderModel.getItemPrice().doubleValue());
        order.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return order;
    }
}
