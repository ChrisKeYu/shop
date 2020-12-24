package top.chris.shop.service;


import top.chris.shop.pojo.OrderItems;
import top.chris.shop.pojo.OrderStatus;
import top.chris.shop.pojo.Orders;
import top.chris.shop.pojo.bo.OrderStatusBo;
import top.chris.shop.pojo.bo.OrdersCreatBo;

import java.util.List;

public interface OrdersService {
    //创建订单
    String CreateOrders(OrdersCreatBo bo);
    //根据订单id查询订单
    Orders queryOrderByOrderId(String orderId);
    //根据订单id查询该订单下所购买商品的详情信息
    List<OrderItems> queryOrderItemsByOrderId(String orderId);
    //根据订单id修改该订单的状态信息
    Integer updateOrderStatusByOrderId(OrderStatusBo bo);
    //根据订单ID查询订单支付状态
    String queryOrderStatusByOrderId(String orderId);
    //根据用户id查询订单状态表中该用户的所有订单状态信息
    List<OrderStatus> queryOrdersStatusByUserId(String userId);
    //根据用户id查询查询订单表的信息
    List<Orders> queryOrderByUserId(String userId);
    //根据userId和OrderStatus查询订单
    List<Orders> queryOrdersByUserIdAndOrderStatus(String userId,String orderStatus);

}
