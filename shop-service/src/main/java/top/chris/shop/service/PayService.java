package top.chris.shop.service;

import com.alipay.api.AlipayApiException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PayService {
    //支付宝支付
    String toPay(String merchantUserId,String merchantOrderId);
    //支付宝回传校验并修改订单状态
    Integer callBackAndUpdateOrderStatus(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AlipayApiException;
}
