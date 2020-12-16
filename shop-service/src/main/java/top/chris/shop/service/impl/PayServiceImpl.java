package top.chris.shop.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.chris.shop.common.AlipayConfig;
import top.chris.shop.pojo.OrderItems;
import top.chris.shop.pojo.Orders;
import top.chris.shop.service.OrdersService;
import top.chris.shop.service.PayService;

import java.util.List;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    OrdersService ordersService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String toPay(String merchantUserId, String merchantOrderId) {
        Orders order = ordersService.queryOrderByOrderId(merchantOrderId);
        List<OrderItems> orderItems = ordersService.queryOrderItemsByOrderId(merchantOrderId);
        String subject = "";
        for (OrderItems orderItem : orderItems) {
            subject += orderItem.getItemName() + "  ";
        }
        //根据传入的orderId，去数据中找到订单信息，然后将订单信息装配给阿里支付平台
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);  //获得初始化的AlipayClient
        AlipayTradePagePayRequest alipayRequest =  new AlipayTradePagePayRequest(); //创建API对应的request
        AlipayTradeWapPayModel payModel = new AlipayTradeWapPayModel();
        payModel.setOutTradeNo(order.getId());//商品ID
        payModel.setSubject(subject);//商品名称
        payModel.setTotalAmount(Integer.toString(order.getTotalAmount()/100));//支付金额
        payModel.setProductCode("FAST_INSTANT_TRADE_PAY");//商品code：在沙箱环境是固定的
        //设置商品参数
        alipayRequest.setBizModel(payModel);
        //支付宝完成交易后，告知支付结果到本后台中的网页/接口，这里暂时写接口（可选）
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        //alipayRequest.setNotifyUrl( "http://domain.com/CallBack/notify_url.jsp" ); //在公共参数中设置回跳和通知地址

        String form= "" ;
        try  {
            form = alipayClient.pageExecute(alipayRequest).getBody();  //调用SDK生成表单
        }  catch  (AlipayApiException e) {
            e.printStackTrace();
        }
        //返回支付宝写好的form表达页面给前端vue弹起支付
        return form;
    }
}
