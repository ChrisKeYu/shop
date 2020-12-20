package top.chris.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.enums.OrderIsCommentEnum;
import top.chris.shop.mapper.ItemsCommentsMapper;
import top.chris.shop.mapper.OrdersMapper;
import top.chris.shop.pojo.ItemsComments;
import top.chris.shop.pojo.OrderItems;
import top.chris.shop.pojo.Orders;
import top.chris.shop.pojo.bo.ItemsCommentsBo;
import top.chris.shop.pojo.vo.ItemCommentVo;
import top.chris.shop.service.MyCommentService;
import top.chris.shop.service.OrdersService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MyCommentServiceImpl implements MyCommentService{
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private ItemsCommentsMapper commentsMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 获取指定订单下商品的评论信息
     * @param userId 用户id
     * @param orderId 订单id
     * @return
     */
    @Override
    public List<ItemCommentVo> queryOrderCommentByUserIdAndOrderId(String userId, String orderId) {
        //1、通过orderId获取到该orderId下购买的所有商品项。
        List<OrderItems> orderItems = ordersService.queryOrderItemsByOrderId(orderId);
        //2、创建容器存储订单内商品的评论对象
        List<ItemCommentVo> itemCommentVos = new ArrayList<ItemCommentVo>();
        for (OrderItems orderItem : orderItems) {
            ItemCommentVo vo = new ItemCommentVo();
            vo.setItemName(orderItem.getItemName());
            vo.setItemSpecName(orderItem.getItemSpecName());
            vo.setItemImg(orderItem.getItemImg());
            Example example = new Example(ItemsComments.class);
            example.createCriteria().andEqualTo("userId",userId).andEqualTo("itemSpecId",orderItem.getItemSpecId());
            //只会返回一个唯一的对象，因此直接get(0)获取即可
            List<ItemsComments> itemsComments = commentsMapper.selectByExample(example);
            vo.setContent(itemsComments.get(0).getContent());
            //评论区的id号
            vo.setId(itemsComments.get(0).getId());
            itemCommentVos.add(vo);
        }
        return itemCommentVos;
    }

    /**
     * 根据前端传入的评论id，修改评论表中对应的评论内容和等级,最后还要修改对应订单中的是否评论的状态
     * @param orderId 订单id
     * @param bo 接受前端传入的评论信息
     * @return
     */
    @Override
    public Integer updateOrderCommentByCommentId(String orderId,List<ItemsCommentsBo> bo) {
        Integer result = 0;
        for (ItemsCommentsBo commentsBo : bo) {
            //取出指定id的评论对象
            ItemsComments itemsComments = commentsMapper.selectByPrimaryKey(commentsBo.getId());
            //修改评论对象的内容
            itemsComments.setCommentLevel(commentsBo.getCommentLevel());
            itemsComments.setContent(commentsBo.getContent());
            itemsComments.setUpdatedTime(new Date());
            Integer count  = commentsMapper.updateByPrimaryKey(itemsComments);
            result += count;
        }
        //修改完数据后，还要把对应订单的中的is_comment项改为true
        Orders orders = ordersService.queryOrderByOrderId(orderId);
        orders.setIsComment(OrderIsCommentEnum.COMMENT.type);
        orders.setUpdatedTime(new Date());
        ordersMapper.updateByPrimaryKey(orders);

        return result;
    }
}
