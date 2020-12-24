package top.chris.shop.mapper;

import top.chris.shop.pojo.Orders;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*   注意：如果返回值为List，那么resultType写对应的对象，且切记一定不要写xx.*，要把所有数据都写出来，这样才可以进行映射： ord.id AS id,
* @author mapper-generator
*/
public interface OrdersMapper extends tk.mybatis.mapper.common.Mapper<Orders> {
    //根据用户id和订单状态查询订单表中的订单数据
    List<Orders> queryOrderByUserIdAndOrderStatus(String userId,String orderStatus);

}




