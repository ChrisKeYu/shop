package top.chris.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.common.OrdersStatusCommon;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.enums.CommentLevelEnum;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.exception.OrdersException;
import top.chris.shop.mapper.ItemsCommentsMapper;
import top.chris.shop.mapper.OrderStatusMapper;
import top.chris.shop.mapper.OrdersMapper;
import top.chris.shop.pojo.ItemsComments;
import top.chris.shop.pojo.OrderItems;
import top.chris.shop.pojo.OrderStatus;
import top.chris.shop.pojo.Orders;
import top.chris.shop.pojo.vo.OrderInfoVo;
import top.chris.shop.pojo.vo.OrderItemInfoVo;
import top.chris.shop.pojo.vo.OrderStatusCountsVo;
import top.chris.shop.pojo.vo.OrderTrendVo;
import top.chris.shop.service.MyOrdersService;
import top.chris.shop.service.OrdersService;
import top.chris.shop.util.PagedGridResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log
@Service
public class MyOrdersServiceImpl implements MyOrdersService {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private Sid sid;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private ItemsCommentsMapper commentsMapper;
    @Autowired
    private PageHelper pageHelper;

    /**
     * 根据userId查询用户所有不同订单状态下的订单个数
     * @param userId 用户id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVo queryOrderStatusCounts(String userId) {
        //根据用户id获取订单状态表中的数据
        List<OrderStatus> statusList = ordersService.queryOrdersStatusByUserId(userId);
        OrderStatusCountsVo vo = new OrderStatusCountsVo();
        vo.setWaitPayCounts(0);
        vo.setWaitReceiveCounts(0);
        vo.setWaitDeliverCounts(0);
        vo.setWaitCommentCounts(0);
        //统计在四个不同状态下，各个订单的数量
        for (OrderStatus orderStatus : statusList) {
            //1、统计未支付的订单数量
            if (orderStatus.getOrderStatus() == OrdersStatusEnum.WAIT_PAY.type){
                vo.setWaitPayCounts(vo.getWaitPayCounts()+1);
            }
            //2、统计未发货的订单数量
            if (orderStatus.getOrderStatus() >= OrdersStatusEnum.PAID.type && orderStatus.getOrderStatus() < OrdersStatusEnum.DELIVERED.type){
                vo.setWaitDeliverCounts(vo.getWaitDeliverCounts()+1);
            }
            //3、统计未收货的订单数量(只要是未发货的状态，就一定除于未收获的转台)
            if (orderStatus.getOrderStatus() == OrdersStatusEnum.DELIVERED.type){
                vo.setWaitReceiveCounts(vo.getWaitReceiveCounts()+1);
            }
            //4、统计待评价的订单数量
            //根据orderId从数据库中获取对应Order信息
            Orders orders = ordersService.queryOrderByOrderId(orderStatus.getOrderId());
            if (orderStatus.getOrderStatus() >= OrdersStatusEnum.SUCCESS.type && orders.getIsComment() == 0){
                vo.setWaitCommentCounts(vo.getWaitCommentCounts()+1);
            }
        }

        return vo;
    }

    /**
     * 查询某位用户的订单动向的查询，返回CommentRecordVo集合的数据模型。
     * @param userId 用户id
     * @param page 当前页面
     * @param pageSize 页面大小
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryOrdersTrendByUserId(String userId, Integer page, Integer pageSize) {
        //根据userId查询用户的订单表信息
        List<Orders> orders = ordersService.queryOrderByUserId(userId);
        //2、创建一个List集合存储指定用户下的所有订单id
        List<String> ordersIds = new ArrayList<String>();
        //3、遍历orders集合获取该用户下的订单id,将其封装起来
        for (Orders order : orders) {
            ordersIds.add(order.getId());
        }
        //4、根据ordersIds去orderStatus表中获取数据
        Example example = new Example(OrderStatus.class);
        example.createCriteria().andIn("orderId",ordersIds);
        //使用pageHelper进行分页查询的设定page和pagesize
        pageHelper.startPage(page,pageSize);
        List<OrderStatus> statusList = orderStatusMapper.selectByExample(example);
        //把查询到数据进行封装
        List<OrderTrendVo> result = new ArrayList<OrderTrendVo>();
        for (OrderStatus orderStatus : statusList) {
            OrderTrendVo vo = new OrderTrendVo();
            vo.setOrderId(orderStatus.getOrderId());
            vo.setOrderStatus(orderStatus.getOrderStatus());
            vo.setPayTime(orderStatus.getPayTime());
            vo.setDeliverTime(orderStatus.getDeliverTime());
            vo.setSuccessTime(orderStatus.getSuccessTime());
            result.add(vo);
        }
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (result != null){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(statusList);
            //插入查询到的指定数据，与上面传入的参数是不一致的，下面传入的参数是orders对象被pageHelper设定固定大小后返回的结果，再由返回的结果查询到的具体数据。
            pagedGridResult.setRows(result);
            //插入当前页数
            pagedGridResult.setPage(page);
            //插入查询的总记录数
            pagedGridResult.setRecords(pageInfo.getTotal());
            //插入总页数（总记录数 / pageSize[每一页的可展示的数量]）
            pagedGridResult.setTotal(pageInfo.getPages());
            log.info("查看分页情况："+ ReflectionToStringBuilder.toString(pagedGridResult));
        }
        return pagedGridResult;
    }

    /**
     * 根据前端传入的不同订单状态码，查询某位用户下的不同状态码的订单信息。
     * @param userId 用户id
     * @param orderStatus 前端传入的订单状态码
     * @param page 当前页面
     * @param pageSize 页面大小
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryOrdersItemsInfoByUserId(String userId, String orderStatus, Integer page, Integer pageSize) {
        if (orderStatus.equals("")){
            //使用pageHelper进行分页查询的设定page和pagesize
            pageHelper.startPage(page,pageSize);
            //根据用户userID查询订单信息
            List<Orders> orders = ordersService.queryOrderByUserId(userId);
            //询指定订单状态下的订单商品信息
            List<OrderInfoVo> orderInfoVos = queryOrdersItemsInfoByOrderIdAndorderStatus(orders);
            //封装分页信息
            PagedGridResult pagedGridResult = savePagedGridResult(orderInfoVos, orders, page);
            //返回分页信息
            return pagedGridResult;
        }else {
            //使用pageHelper进行分页查询的设定page和pagesize
            pageHelper.startPage(page,pageSize);
            //根据用户userID和OrderStatus查询订单信息
            List<Orders> orders = ordersService.queryOrdersByUserIdAndOrderStatus(userId,orderStatus);
            //询指定订单状态下的订单商品信息
            List<OrderInfoVo> orderInfoVos = queryOrdersItemsInfoByOrderIdAndorderStatus(orders);
            //封装分页信息
            PagedGridResult pagedGridResult = savePagedGridResult(orderInfoVos, orders, page);
            //返回分页信息
            return pagedGridResult;
        }
    }

    /**
     * 根据userId和OrderId修改订单状态，根据userId和OrderId修改订单状态，而且把该订单的商品写入到商品评论表中，方便下次点击该订单评论的时候可以在评论表中提取对应商品然后修改商品评论内容和等级。
     * @param userId 用户id
     * @param orderId 订单id
     * @param orderStatus 状态码
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer updateOrderStatusByUserIdAndOrderId(String userId, String orderId,Integer orderStatus) {
        //根据id获取数据库中存储的订单状态数据
        OrderStatus status = orderStatusMapper.selectByPrimaryKey(orderId);
        if (status == null){
            throw new OrdersException("订单状态表中没有订单号为："+orderId+"的订单状态信息");
        }
        status.setOrderStatus(orderStatus);
        //在订单商品确定收货后，就要把该商品插入到评论表中，这样才可以在点击收货货，在数据库的评论列表中看到这个待评论的商品，否则，当你点击评论时，无法根据orderid和userid在评论表中查询到待评论的对象
        //1、获取该订单下的商品项
        List<OrderItems> orderItems = ordersService.queryOrderItemsByOrderId(orderId);
        //2、将订单下的商品项写入到评论表中
        for (OrderItems orderItem : orderItems) {
            ItemsComments itemsComments = new ItemsComments();
            itemsComments.setId(sid.nextShort());
            itemsComments.setUserId(userId);
            itemsComments.setItemId(orderItem.getItemId());
            itemsComments.setItemSpecId(orderItem.getItemSpecId());
            itemsComments.setItemName(orderItem.getItemName());
            itemsComments.setSepcName(orderItem.getItemSpecName());
            //默认为空
            itemsComments.setContent("");
            //默认好评
            itemsComments.setCommentLevel(CommentLevelEnum.GOOD.type);
            itemsComments.setCreatedTime(new Date());
            itemsComments.setUpdatedTime(new Date());
            //将已完成的订单下的待评论的商品插入到数据库的评论表中
            commentsMapper.insert(itemsComments);
        }
        return orderStatusMapper.updateByPrimaryKey(status);
    }

    /**
     * 查询指定订单状态下的订单商品信息
     * @param orders 根据userId，在Order表中查询到的所有该用户下的订单数据
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrderInfoVo> queryOrdersItemsInfoByOrderIdAndorderStatus(List<Orders> orders){
        //构建存储订单详细信息Vo对象的List集合
        List<OrderInfoVo> orderInfoVos = new ArrayList<OrderInfoVo>();
        for (Orders order : orders) {
            orderInfoVos = queryOrdersItems(order,orderInfoVos);
        }
        return orderInfoVos;
    }

    /**
     * 封装不同订单状态下的订单数据
     * @param order 不同订单状态下的订单对象
     * @param orderInfoVos 存储订单对象的容器
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public  List<OrderInfoVo> queryOrdersItems(Orders order,List<OrderInfoVo> orderInfoVos){
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        orderInfoVo.setOrderId(order.getId());
        orderInfoVo.setCreatedTime(order.getCreatedTime());
        orderInfoVo.setIsComment(order.getIsComment());
        orderInfoVo.setPayMethod(order.getPayMethod());
        orderInfoVo.setPostAmount(order.getPostAmount());
        orderInfoVo.setRealPayAmount(order.getRealPayAmount());
        orderInfoVo.setOrderStatus(ordersService.queryOrderStatusByOrderId(order.getId()));
        //存储订单下面的具体商品信息
        List<OrderItemInfoVo> orderItemInfoVos = new ArrayList<OrderItemInfoVo>();
        for (OrderItems orderItem : ordersService.queryOrderItemsByOrderId(order.getId())) {
            OrderItemInfoVo vo = new OrderItemInfoVo();
            vo.setPrice(orderItem.getPrice());
            vo.setItemId(orderItem.getItemId());
            vo.setItemImg(orderItem.getItemImg());
            vo.setItemName(orderItem.getItemName());
            vo.setBuyCounts(orderItem.getBuyCounts());
            vo.setItemSpecName(orderItem.getItemSpecName());
            orderItemInfoVos.add(vo);
        }
        orderInfoVo.setSubOrderItemList(orderItemInfoVos);
        orderInfoVos.add(orderInfoVo);
        return orderInfoVos;
    }

    /**
     * 专门用于做分页后结果的封装
     * @param orderInfoVos 根据分页输出的结果，查询该结果集下面的所有订单信息
     * @param orders   分页输出的结果：包含{总记录数、分页记录结果}
     * @param page  当前页面大小
     * @return
     */
    public PagedGridResult savePagedGridResult( List<OrderInfoVo> orderInfoVos,List<Orders> orders,Integer page){
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (orderInfoVos != null){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(orders);
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
