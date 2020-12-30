package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminItemSpecBo {
    private String itemId;
    //规格名称：3kg/箱、6kg/箱、10kg/箱
    private String name;
    //库存
    private Integer stock;
    //折扣力度
    private BigDecimal discounts;
    //优惠价（原价*折扣力度，不用从前端获取）
    //private Integer priceDiscount;
    //原价
    private Integer priceNormal;





}
