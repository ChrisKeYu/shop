package top.chris.shop.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//购物项数据:用于描述一个商品在购物车的数据展示
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemVo {
    private String cartId;
    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private Integer priceDiscount;
    private Integer priceNormal;
    private Integer buyCounts;

    /**
     * 计算单个购物项价格（小计）：优惠价格*购买数量,(因为管理后台在上架商品的时候会将原价和折扣输入，
     *                            然后自动计算优惠价格，最后写入数据库，因此这里直接取优惠价格计算)
     * @return
     */
    public Integer getCartItemSubTotal(){
        return priceDiscount*buyCounts;
    }







}
