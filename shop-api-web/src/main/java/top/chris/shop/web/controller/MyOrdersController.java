package top.chris.shop.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.pojo.vo.OrderStatusCountsVo;
import top.chris.shop.service.MyOrdersService;
import top.chris.shop.util.JsonResult;

import java.util.List;

@Api("个人中心的订单控制器，管理订单")
@RestController
@RequestMapping("/myorders")
public class MyOrdersController {
    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation("查询指定用户订单状态数")
    @PostMapping("/statusCounts")
    public JsonResult getOrderStatusCounts(String userId){
        OrderStatusCountsVo vo = myOrdersService.queryOrderStatusCounts(userId);
        return JsonResult.isOk(vo);
    }

    @ApiOperation("查询指定用户订单动向-分页查询")
    @PostMapping("/trend")
    public JsonResult getOrderTrend(String userId,@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(myOrdersService.queryOrdersTrendByUserId(userId,page,pageSize));
    }

    @ApiOperation("查询指定用户的所有订单详细信息-分页查询")
    @PostMapping("/query")
    public JsonResult getOrdersItemsInfo(String userId,String orderStatus,@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(myOrdersService.queryOrdersItemsInfoByUserId(userId,orderStatus,page,pageSize));
    }

    @ApiOperation("根据用户id和订单id，确认用户收货")
    @PostMapping("/confirmReceive")
    public JsonResult confirmReceive(String userId,String orderId){
        //确认用户收货的状态码
        Integer orderStatus = OrdersStatusEnum.SUCCESS.type;
        return JsonResult.isOk(myOrdersService.updateOrderStatusByUserIdAndOrderId(userId,orderId,orderStatus));
    }
}
