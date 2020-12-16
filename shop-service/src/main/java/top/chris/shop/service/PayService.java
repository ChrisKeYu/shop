package top.chris.shop.service;

public interface PayService {
    //支付宝支付
    String toPay(String merchantUserId,String merchantOrderId);
}
