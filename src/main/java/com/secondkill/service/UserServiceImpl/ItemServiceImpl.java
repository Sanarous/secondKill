package com.secondkill.service.UserServiceImpl;

import com.secondkill.dao.ItemMapper;
import com.secondkill.dao.ItemStockMapper;
import com.secondkill.dataobject.Item;
import com.secondkill.dataobject.ItemStock;
import com.secondkill.error.BusinessException;
import com.secondkill.error.EmBusinessError;
import com.secondkill.service.ItemService;
import com.secondkill.service.model.ItemModel;
import com.secondkill.validator.ValidationResult;
import com.secondkill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品列表Service
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemStockMapper itemStockMapper;

    /**
     * 根据商品id获取商品领域模型
     * @param id  商品id
     * @return 商品领域模型
     */
    @Override
    public ItemModel getItemById(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        if(item == null) return null;
        //获得库存数量
        ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());

        //将dataObject转换成Model
        return convertModelFromObject(item, itemStock);
    }

    /**
     * 落单减库存
     * @param itemId  商品id
     * @param amount  商品数量
     * @return  boolean
     */
    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectedRow = itemStockMapper.decreaseStock(itemId, amount);
        if(affectedRow > 0){
            //表示更新库存成功
            return true;
        }else{
            //表示更新库存失败
            return false;
        }
    }

    /**
     * 增加商品销量
     * @param itemId  商品id
     * @param amount  商品数量
     * @throws Exception  异常
     */
    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws Exception {
        itemMapper.increaseSales(itemId,amount);
    }

    /**
     * 创建商品列表
     * @param itemModel  商品领域模型
     * @return 返回创建的商品领域模型
     * @throws BusinessException  可能抛出参数异常
     */
    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //将领域模型拆分为item实体和itemStock实体并分别插入到数据库中
        Item item = convertItemFromItemModel(itemModel);
        itemMapper.insertSelective(item);

        itemModel.setId(item.getId());

        ItemStock itemStock = convertItemStockFromItemModel(itemModel);
        itemStockMapper.insertSelective(itemStock);

        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    /**
     * 从领域模型转换成商品实体
     * @param itemModel  商品列表领域模型
     * @return 商品实体类
     */
    private Item convertItemFromItemModel(ItemModel itemModel){
        if(itemModel == null) return null;
        Item item = new Item();
        BeanUtils.copyProperties(itemModel,item);
        item.setPrice(itemModel.getPrice().doubleValue());  //参数类型不一致，需要手动设置
        return item;
    }

    /**
     * 从领域模型转换成商品库存实体
     * @param itemModel  商品领域模型
     * @return  商品库存实体类
     */
    private ItemStock convertItemStockFromItemModel(ItemModel itemModel){
        if(itemModel == null) return null;
        ItemStock itemStock = new ItemStock();
        itemStock.setItemId(itemModel.getId());
        itemStock.setStock(itemModel.getStock());
        return itemStock;
    }

    /**
     * 查询商品列表
     * @return 商品列表
     */
    @Override
    public List<ItemModel> listItem() {
        List<Item> itemList = itemMapper.listItem();
        //使用java8的stream的API
        return itemList.stream().map(item -> {
            ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());
            return this.convertModelFromObject(item, itemStock);
        }).collect(Collectors.toList());
    }

    /**
     * 从商品实体类转换成商品领域模型类
     * @param item  商品实体类
     * @param itemStock  商品库存类
     * @return  商品领域模型类
     */
    private ItemModel convertModelFromObject(Item item,ItemStock itemStock){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(item,itemModel);

        itemModel.setPrice(new BigDecimal(item.getPrice()));  //由于price参数的类型不一致，因此copy的时候需要手动设置参数
        itemModel.setStock(itemStock.getStock());
        return itemModel;
    }
}
