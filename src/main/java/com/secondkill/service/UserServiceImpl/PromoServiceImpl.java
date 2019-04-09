package com.secondkill.service.UserServiceImpl;

import com.secondkill.dao.PromoMapper;
import com.secondkill.dataobject.Promo;
import com.secondkill.service.PromoService;
import com.secondkill.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoMapper promoMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        Promo promo = promoMapper.selectByItemId(itemId);

        //转化成商品领域模型
        PromoModel promoModel = convertFromDataObject(promo);
        if(promoModel == null)  return null;

        //判断当前时间是否秒杀活动即将开始或正在进行
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(2);
        }else{
            promoModel.setStatus(3);
        }
        return promoModel;
    }

    private PromoModel convertFromDataObject(Promo promo){
        if(promo == null) return null;

        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promo,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promo.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promo.getStartDate()));
        promoModel.setEndDate(new DateTime(promo.getEndDate()));

        return promoModel;
    }
}
