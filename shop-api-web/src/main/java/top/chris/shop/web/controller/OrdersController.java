package top.chris.shop.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.pojo.bo.OrdersCreatBo;
import top.chris.shop.service.OrdersService;
import top.chris.shop.util.CookieUtils;
import top.chris.shop.util.JsonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Api("订单控制器，管理订单")
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ShopProperties shopProperties;

    @Autowired
    private ObjectMapper objectMapper;

    //TODO:提交的订单中，缺少了购买数量，后期把购物车在后台实现后，就从购物车数据库中取数据
    @ApiOperation("创建订单")
    @PostMapping("/create")
    public JsonResult creatOrder(@RequestBody OrdersCreatBo bo,HttpServletRequest request, HttpServletResponse response){
        //从请求头中传递数据
        //创建订单
        String orderId = ordersService.CreateOrders(bo);
       /* //创建好订单后就更新前端保存的购物车数据
        //将订单中商品的id进行切割
        String[] itemSpecIds = StringUtils.splitByWholeSeparatorPreserveAllTokens(bo.getItemSpecIds(),",");
        //获取到切割后的商品id，然后再把cookie中的商品id取出来，只要两者的id相同，那么就去除，只保留不同商品id的，然后写回到购物车中
        System.out.println("获取购物车中的所有商品ids："+CookieUtils.getCookieValue(request,shopProperties.getShopCarCookieName(),true));
        //CookieUtils.setCookie(request,response,shopProperties.getShopCarCookieName(),"");*/
        return JsonResult.isOk(orderId);
    }

    @ApiOperation("查询订单支付状态")
    @PostMapping("/getPaidOrderInfo")
    public JsonResult getPaidOrderInfo(String orderId){
        System.out.println("-----轮询订单"+orderId+"是否支付成功");
        return JsonResult.isOk(ordersService.queryOrderStatusByOrderId(orderId));
    }

}
