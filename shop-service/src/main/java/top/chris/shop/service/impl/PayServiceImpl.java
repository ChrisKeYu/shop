package top.chris.shop.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.chris.shop.common.AlipayConfig;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.pojo.OrderItems;
import top.chris.shop.pojo.Orders;
import top.chris.shop.pojo.bo.OrderStatusBo;
import top.chris.shop.service.OrdersService;
import top.chris.shop.service.PayService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    OrdersService ordersService;

    /**
     * 调起支付宝支付
     * @param merchantUserId 用户id
     * @param merchantOrderId 订单id
     * @return
     */
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

    /**
     * 支付宝回传校验并修改订单状态
     * @param httpRequest
     * @param httpResponse
     * @return
     * @throws AlipayApiException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer callBackAndUpdateOrderStatus(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AlipayApiException {
        //商品的订单号
        String out_trade_no = null;
        //影响行数
        Integer result = 0;
        Map<String, String[]> parameterMap = httpRequest.getParameterMap();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            //从支付宝返回的请求中拿到订单号
            if (entry.getKey().equals("out_trade_no")){
                String[] value = entry.getValue();
                out_trade_no = value[0];
            }
        }
        //校验支付宝返回的数据是否被篡改
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);  //获得初始化的AlipayClient
        AlipayTradeWapPayModel payModel = new AlipayTradeWapPayModel();
        payModel.setOutTradeNo(out_trade_no);//带查询的订单号
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(payModel);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){ //校验成功，数据没有被篡改
            //获取用户支付的交易状态
            String tradeStatus = response.getTradeStatus();
            if (StringUtils.equals("TRADE_SUCCESS",tradeStatus)){
                OrderStatusBo orderStatusBo = new OrderStatusBo();
                orderStatusBo.setOrderId(out_trade_no);
                orderStatusBo.setOrderStatus(OrdersStatusEnum.PAID.type);
                orderStatusBo.setPayTime(new Date());
                //付款成功：修改订单的状态码为已付款状态
                result = ordersService.updateOrderStatusByOrderId(orderStatusBo);
                //最后可以选择跳转到个人中心/订单中心/主页/支付成功页面(跳转交给前端)
            }else {
                System.out.println("交易失败");
                //付款未成功：全额退款、待支付
                //最后选择跳转到个人中心/订单中心
            }
            System.out.println("-----调用成功");
        } else {//校验失败，数据被被篡改，属于非法请求
            System.out.println("-----调用失败,非法请求");
        }
        return result;
    }
}
