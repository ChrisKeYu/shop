package top.chris.shop.service;

import top.chris.shop.pojo.bo.OrdersCreatBo;

public interface OrdersService {
    //创建订单
    public String CreateOrders(OrdersCreatBo bo);
}
