package top.chris.shop.service;

import top.chris.shop.pojo.bo.ItemsCommentsBo;
import top.chris.shop.pojo.vo.ItemCommentVo;

import java.util.List;

public interface MyCommentService {
    //获取指定订单下商品的评论信息
    List<ItemCommentVo> queryOrderCommentByUserIdAndOrderId(String userId,String orderId);
    //根据前端传入的评论id，修改评论表中对应的评论内容和等级,最后还要修改对应订单中的是否评论的状态
    Integer updateOrderCommentByCommentId(String orderId,List<ItemsCommentsBo> bo);
}
