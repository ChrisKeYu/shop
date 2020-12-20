package top.chris.shop.pojo.vo;

import lombok.Data;
//购物完成后，对订单商品的评价对象
@Data
public class ItemCommentVo {
    //商品id(itemId) 不应该是商品id！，或许真的是评论区的id号码
    private String id;
    //已购买商品时商品图片
    private String itemImg;
    //商品名称
    private String itemName;
    //商品规格名称
    private String itemSpecName;
    //评价内容
    private String content;
}
