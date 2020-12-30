package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

@Data
public class AdminItemBo {
    private String itemId;
    private String itemName;
    //最细的级分类
    private Integer catId;
    //一级分类
    private Integer rootCatId;
    //上下架
    private Integer onOffStatus;
    //商品简介
    private String content;
}
