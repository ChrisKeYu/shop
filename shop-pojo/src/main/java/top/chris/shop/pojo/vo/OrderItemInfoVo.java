package top.chris.shop.pojo.vo;

import lombok.Data;

@Data
public class OrderItemInfoVo {
    //商品id
   private String itemId;
    //商品图片
    private String itemImg;
    //商品名称
    private String itemName;
    //规格名称
    private String itemSpecName;
    //单价
    private Integer price;
    //购买数量
    private Integer buyCounts;

}
