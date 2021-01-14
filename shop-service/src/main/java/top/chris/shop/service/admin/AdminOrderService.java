package top.chris.shop.service.admin;

import org.springframework.web.bind.annotation.RequestParam;
import top.chris.shop.util.PagedGridResult;

public interface AdminOrderService {
    //根据条件查询订单信息
    PagedGridResult queryOrderItemInfoByCondition(String condition,Integer page, Integer pageSize);

    //根据OrderId查询订单信息
    PagedGridResult queryOrderItemInfoById(String id,Integer page, Integer pageSize);

    //根据订单id修改订单状态
    void updataOrderStatusById(String id,String status);

    //根据订单id删除订单状态
    void delOrderById(String id);
}
