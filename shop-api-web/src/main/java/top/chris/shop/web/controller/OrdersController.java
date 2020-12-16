package top.chris.shop.web.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.io.IOException;

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

    //TODO:提交的订单中，缺少了购买数量，后期把购物车在后台实现后，就从购物车数据库中取数据
    @ApiOperation("创建订单")
    @PostMapping("/create")
    public JsonResult creatOrder(@RequestBody OrdersCreatBo bo){//从请求头中传递数据
        //创建订单
        String orderId = ordersService.CreateOrders(bo);
        //TODO:由于购物车是前端页面用cookie实现的，没有在后台实现，危害：如果用户只结算了其中的某个商品，那么其它的商品也会丢失，后期改为Cookie+Session+数据库完成
        //清空购物车，只要清除浏览器中的cookie
        CookieUtils.setCookie(request,response,shopProperties.getShopCarCookieName(),"");
        return JsonResult.isOk(orderId);
    }
    @ApiOperation("查询订单支付状态")
    @PostMapping("/getPaidOrderInfo")
    public JsonResult getPaidOrderInfo(String orderId){
        System.out.println("-----轮询订单"+orderId+"是否支付成功");
        return JsonResult.isOk(ordersService.queryOrderStatusByOrderId(orderId));
    }





}
