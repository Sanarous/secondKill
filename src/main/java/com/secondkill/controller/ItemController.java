package com.secondkill.controller;

import com.secondkill.controller.viewObject.ItemVO;
import com.secondkill.error.BusinessException;
import com.secondkill.response.CommonReturnType;
import com.secondkill.service.ItemService;
import com.secondkill.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
@CrossOrigin(allowedHeaders = "*",allowCredentials = "true")
public class ItemController extends BaseController{

    @Autowired
    private ItemService itemService;

    /**
     * 创建商品的controller
     * @param title 商品标题
     * @param description  商品描述
     * @param price  商品价格
     * @param stock  商品库存
     * @param imgUrl 商品图片url
     * @return 通用返回类型
     */
    @PostMapping(value = "/create",consumes = {CONTEXT_TYPE_FORMED})
    public CommonReturnType createItem(@RequestParam("title") String title,
                                       @RequestParam("description") String description,
                                       @RequestParam("price")BigDecimal price,
                                       @RequestParam("stock")Integer stock,
                                       @RequestParam("imgUrl") String imgUrl) throws BusinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setImgUrl(imgUrl);
        itemModel.setStock(stock);
        itemModel.setSales(0);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);

    }

    private ItemVO convertVOFromModel(ItemModel itemModel){
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }

    /**
     * 商品详情页浏览
     * @param id  商品id
     * @return  通用返回
     */
    @GetMapping(value = "/get")
    public CommonReturnType getItem(@RequestParam("id")Integer id){
        ItemModel itemModel = itemService.getItemById(id);

        ItemVO itemVO = convertVOFromModel(itemModel);

        return CommonReturnType.create(itemVO);
    }

    /**
     * 商品列表页面浏览
     * @return
     */
    @GetMapping("/list")
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList = itemService.listItem();

        //使用stream api将list内的itemModel转换成ItemVO
        List<ItemVO> itemVOS = itemModelList.stream().map(this::convertVOFromModel).collect(Collectors.toList());
        return CommonReturnType.create(itemVOS);
    }
}
