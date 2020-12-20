package top.chris.shop.service;



import top.chris.shop.pojo.vo.OrderStatusCountsVo;
import top.chris.shop.util.PagedGridResult;


public interface MyOrdersService {
    //根据userId查询用户所有不同订单状态下的订单个数
    OrderStatusCountsVo queryOrderStatusCounts(String userId);

    //订单动向的查询，返回CommentRecordVo集合的数据模型。
    PagedGridResult queryOrdersTrendByUserId(String userId, Integer page, Integer pageSize);

    //查询指定用户的所有订单的详细信息
    PagedGridResult queryOrdersItemsInfoByUserId(String userId,String orderStatus,Integer page, Integer pageSize);

    //根据userId和OrderId修改订单状态
    Integer updateOrderStatusByUserIdAndOrderId(String userId,String orderId,Integer orderStatus);
}
