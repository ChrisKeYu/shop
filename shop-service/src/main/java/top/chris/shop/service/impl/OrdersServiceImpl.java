package top.chris.shop.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.exception.OrdersException;
import top.chris.shop.mapper.OrderItemsMapper;
import top.chris.shop.mapper.OrderStatusMapper;
import top.chris.shop.mapper.OrdersMapper;
import top.chris.shop.pojo.*;
import top.chris.shop.pojo.bo.OrdersCreatBo;
import top.chris.shop.service.AddressService;
import top.chris.shop.service.ItemsService;
import top.chris.shop.service.OrdersService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private Sid sid ;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    //TODO 商品的数量目前统一设置为1，其次，邮费统一设置为10，后期待完善，因为用户没办法一次性买多个商品，所有需要一个购物车后台模块
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String CreateOrders(OrdersCreatBo bo) {
        //统一设置邮费为10元=1000/100，1000的后面两个0是小数
        int postAmount = 1000;
        //订单总价格
        int totalPayAmount = 0;
        //实际支付总价格
        int realPayAmount = 0;

        //创建订单对象
        Orders orders = new Orders();
        orders.setCreatedTime(new Date());
        orders.setLeftMsg(bo.getLeftMsg());
        orders.setId(sid.nextShort());
        orders.setIsComment(0);
        orders.setIsDelete(0);
        orders.setPayMethod(bo.getPayMethod());
        orders.setUserId(bo.getUserId());
        orders.setUpdatedTime(new Date());
        UserAddress address = addressService.queryUserAddressByAddressId(bo.getAddressId());
        if (address == null){
            throw new OrdersException("未找到对应地址");
        }
        //赋值查询到的用户收获地址
        orders.setReceiverAddress(address.getProvince()+" "+address.getCity()+" "+address.getDistrict()+" "+address.getDetail());
        orders.setReceiverMobile(address.getMobile());
        orders.setReceiverName(address.getReceiver());

        //将订单中商品的id进行切割
        String[] itemSpecIds = StringUtils.splitByWholeSeparatorPreserveAllTokens(bo.getItemSpecIds(),",");
        List<String> itemSpecIdList = new ArrayList<String>(itemSpecIds.length);
        //将数组转换为List集合
        CollectionUtils.addAll(itemSpecIdList,itemSpecIds);
        //在数据库中查询该订单中所有的商品类目
        List<ItemsSpec> itemsSpecs = itemsService.queryItemSpecByitemSpecIds(itemSpecIdList);
        //暂时设置默认每个商品的购买数量为1个，因为前端没有传入某一具体类型商品的数量

        for (ItemsSpec itemSpec :itemsSpecs) {
            //固定所有商品购买的数量都为1，后期需要修改。
            int buyCounts = 1;
            //创建具体商品订单对象
            OrderItems orderItems = new OrderItems();
            orderItems.setBuyCounts(buyCounts);
            orderItems.setId(sid.nextShort());
            orderItems.setItemId(itemSpec.getItemId());
            orderItems.setItemImg(itemsService.queryItemImgByItemId(itemSpec.getItemId()).getUrl());
            orderItems.setItemName(itemsService.queryItemByItemId(itemSpec.getItemId()).getItemName());
            orderItems.setOrderId(orders.getId());
            orderItems.setPrice(itemSpec.getPriceDiscount());
            orderItems.setItemSpecId(itemSpec.getId());
            orderItems.setItemSpecName(itemSpec.getName());
            //插入到OrderItems表中
            orderItemsMapper.insert(orderItems);
            //计算订单总价格
            totalPayAmount += orderItems.getPrice()*orderItems.getBuyCounts();
            //将商品的库存设置为购买的数量，目的是用于记录购买数量，方便扣除库存
            itemSpec.setStock(buyCounts);
            //根据商品id，在数据库中把对应商品的库存进行扣除。
            itemsService.decreaseItemSpecStock(itemSpec);

        }
        //订单总价格加上邮费
        totalPayAmount += postAmount;
        //计算实际支付总价格
        realPayAmount = totalPayAmount;

        //赋值查询关于商品价格的信息
        orders.setPostAmount(postAmount);
        orders.setRealPayAmount(realPayAmount);
        orders.setTotalAmount(totalPayAmount);
        //将订单对象插入到数据库的订单表中
        ordersMapper.insert(orders);

        //创建订单状态对象
        OrderStatus status = new OrderStatus();
        status.setCloseTime(null);
        status.setCommentTime(null);
        status.setCreatedTime(new Date());
        status.setDeliverTime(null);
        status.setOrderId(orders.getId());
        status.setPayTime(null);
        status.setSuccessTime(null);
        status.setOrderStatus(OrdersStatusEnum.WAIT_PAY.type);
        //将订单状态插入到订单状态表中
        orderStatusMapper.insert(status);

        return orders.getId();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryOrderByOrderId(String orderId) {
        return ordersMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryOrderItemsByOrderId(String orderId) {
        Example example = new Example(OrderItems.class);
        example.createCriteria().andEqualTo("orderId",orderId);
        return orderItemsMapper.selectByExample(example);
    }
}
