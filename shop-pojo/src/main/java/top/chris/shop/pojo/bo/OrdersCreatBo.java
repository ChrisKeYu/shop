package top.chris.shop.pojo.bo;

import lombok.Data;

@Data
public class OrdersCreatBo {
    //用户Id
    private String userId;
    //订单中商品的ID
    private String itemSpecIds;
    //用户指定收获地址id
    private String addressId;
    //用户留言
    private String leftMsg;
    //支付方式
    private Integer payMethod;

}
