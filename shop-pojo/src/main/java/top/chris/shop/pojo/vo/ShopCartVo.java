package top.chris.shop.pojo.vo;

import lombok.Data;

@Data
public class ShopCartVo {
  //具体规格商品的id
  private String  specId;
  private String  itemImgUrl ;
  private String  itemName ;
  private String  specName ;
  //商品价格存储到数据库时乘上100，使得小数点扩大两位，输出到前端的时候再除于100，恢复到正常价格
  private Integer priceNormal ;
  private Integer priceDiscount ;
}
