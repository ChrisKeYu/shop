package top.chris.shop.service;


import top.chris.shop.pojo.OrderItems;
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
}
