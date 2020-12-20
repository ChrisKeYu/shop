package top.chris.shop.pojo.bo;

import lombok.Data;

import java.util.List;

//用于接受前端传入的商品评论内容
@Data
public class ItemsCommentsBo {
    /**
     * commentId: 0
     * commentLevel: 1
     * content: "测试1"
     * id: "201220G7TNC6FW00"
     * itemImg: "http://122.152.205.72:88/foodie/cake-1001/img1.png"
     * itemName: "【天天吃货】真香预警 超级好吃 手撕面包 儿童早餐早饭"
     * itemSpecName: "原味"
     */
    //评论id
    private String id;
    //评论等级
    private Integer commentLevel;
    //评论内容
    private String  content;
}
