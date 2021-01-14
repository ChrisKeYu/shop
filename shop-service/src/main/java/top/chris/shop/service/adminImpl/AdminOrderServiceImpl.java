package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.mapper.OrderStatusMapper;
import top.chris.shop.mapper.OrdersMapper;
import top.chris.shop.pojo.OrderStatus;
import top.chris.shop.pojo.Orders;
import top.chris.shop.pojo.vo.OrderInfoVo;
import top.chris.shop.service.admin.AdminOrderService;
import top.chris.shop.util.PagedGridResult;

import java.util.Date;
import java.util.List;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper statusMapper;

    @Autowired
    private PageHelper pageHelper;

    @Override
    public PagedGridResult queryOrderItemInfoByCondition(String condition, Integer page, Integer pageSize) {
        pageHelper.startPage(page,pageSize);
        List<OrderInfoVo> orderInfoVos = ordersMapper.queryOrderItemInfoByCondition(condition);
        return savePagedGridResult(orderInfoVos,page);
    }

    @Override
    public PagedGridResult queryOrderItemInfoById(String id, Integer page, Integer pageSize) {
        pageHelper.startPage(page,pageSize);
        List<OrderInfoVo> orderInfoVos = ordersMapper.queryOrderItemInfoById(id);
        return savePagedGridResult(orderInfoVos,page);
    }

    @Override
    public void updataOrderStatusById(String id, String status) {
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(id);
        orderStatus.setDeliverTime(new Date());
        orderStatus.setOrderStatus(OrdersStatusEnum.DELIVERED.type);
        statusMapper.updateByPrimaryKey(orderStatus);
    }

    @Override
    public void delOrderById(String id) {
        Orders orders = ordersMapper.selectByPrimaryKey(id);
        orders.setIsDelete(1);
        orders.setUpdatedTime(new Date());
        ordersMapper.updateByPrimaryKey(orders);
    }

    /**
     * 专门用于做分页后结果的封装
     * @param orderInfoVos 根据分页输出的结果，查询该结果集下面的所有订单信息
     * @param page  当前页面大小
     * @return
     */
    public PagedGridResult savePagedGridResult(List<OrderInfoVo> orderInfoVos, Integer page){
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (orderInfoVos != null){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(orderInfoVos);
            //插入查询到的指定数据，与上面传入的参数是不一致的，下面传入的参数是orders对象被pageHelper设定固定大小后返回的结果，再由返回的结果查询到的具体数据。
            pagedGridResult.setRows(orderInfoVos);
            //插入当前页数
            pagedGridResult.setPage(page);
            //插入查询的总记录数
            pagedGridResult.setRecords(pageInfo.getTotal());
            //插入总页数（总记录数 / pageSize[每一页的可展示的数量]）
            pagedGridResult.setTotal(pageInfo.getPages());
        }
        return pagedGridResult;
    }
}
