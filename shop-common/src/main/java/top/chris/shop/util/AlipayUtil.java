package top.chris.shop.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StreamUtil;

import com.alipay.api.internal.util.codec.Base64;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 支付宝PC端支付API的简便封装
 * @author TOOBUG
 * @version V1.0
 * @Package com.mayunshe.utils
 * @date 2020/5/26 11:52 上午
 * @Copyright © 2020-2021 ChrisCompany
 */
public class AlipayUtil {
    private static  AlipayClient ALIPAY_CLIENT_PAGE=null;
    private static  AlipayClient ALIPAY_CLIENT_QUERY=null;
    private static String appid=null;
    private static String serverUrl=null;
    private static String privateKey=null;
    private static String publicKey=null;
    private static String aliPublicKey=null;
    private static String format=null;
    private static String charset=null;
    private static String signType=null;
    private static String returnUrl=null;
    //private static String notifyUrl=null;

    //读取配置文件的配置项信息
    static {
        ResourceBundle alipay_config = ResourceBundle.getBundle("alipay");
        appid = alipay_config.getString("appid");
        serverUrl=alipay_config.getString("serverUrl");
        privateKey=alipay_config.getString("privateKey");
        publicKey=alipay_config.getString("publicKey");
        format=alipay_config.getString("format");
        charset=alipay_config.getString("charset");
        signType=alipay_config.getString("signType");
        returnUrl=alipay_config.getString("returnUrl");
        aliPublicKey=alipay_config.getString("aliPublicKey");

        ALIPAY_CLIENT_PAGE= new DefaultAlipayClient(serverUrl,appid,privateKey,format,charset,publicKey,signType);
        ALIPAY_CLIENT_QUERY= new DefaultAlipayClient(serverUrl,appid,privateKey,format,charset,aliPublicKey,signType);
    }


    /**
     * 支付宝下单接口调用
     * @param orderId 订单号
     * @param orderSubject 订单描述
     * @param price 订单价格
     * @return 阿里SDK表单内容
     * @throws AlipayApiException
     * @throws JsonProcessingException
     */
    public static String generateAlipayTradePagePayRequestForm(String orderId,String orderSubject,double price) throws AlipayApiException, JsonProcessingException {
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(returnUrl);
        //alipayRequest.setNotifyUrl(notifyUrl);//在公共参数中设置回跳和通知地址
        BizContent bizContent = new BizContent(orderId, price, orderSubject);
        String s = new ObjectMapper().writeValueAsString(bizContent);
        alipayRequest.setBizContent(s);
        return ALIPAY_CLIENT_PAGE.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
    }

    public static boolean generateAlipayTradeQueryRequest(String orderId) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\""+orderId+"\"" +
                "  }");
        AlipayTradeQueryResponse response = ALIPAY_CLIENT_QUERY.execute(request);
        if(response.isSuccess()){
            String tradeStatus = response.getTradeStatus();
            if (TradeStatus.TRADE_SUCCESS.name().equals(tradeStatus)){
                return true;
            }
            System.out.println(tradeStatus);
        }

        return false;
    }

    /**
     * 支付宝验签校验
     * @param requestParams 获取支付宝响应参数集合
     * @return 验签结果
     */
    public static boolean check(Map<String,String[]> requestParams){
        try {
            return AlipaySignature.rsaCheckV1(convert(requestParams), aliPublicKey, charset, signType);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 支付宝验签校验
     * @param requestParams 获取支付宝响应参数集合
     * @return 验签结果
     */
    public static boolean check_(Map<String,String[]> requestParams){
        try {
            return AlipaySignature.rsaCheckV2(convert(requestParams), aliPublicKey, charset, signType);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws AlipayApiException, JsonProcessingException {
        //String s = generateAlipayTradePagePayRequestForm("34E01527317D4F54AB740E235DC35F11", "这是我的订单", 0.01);
        //System.out.println(s);
        boolean b = generateAlipayTradeQueryRequest("ABAAFA7C7E434080A4C9D4918871F718");
        System.out.println(b);

    }

    /**
     * Map<String,String[]> 转换为 Map<String,String>
     * @param requestParams 需要转换的的Map集合
     * @return 转换后的Map集合
     */
    private static Map<String,String> convert(Map<String,String[]> requestParams){
        Map<String,String> result=new HashMap<>();
        if (requestParams!=null&&requestParams.size()>0){
            Set<Map.Entry<String, String[]>> entrySet = requestParams.entrySet();
            for (Map.Entry<String, String[]> entry : entrySet) {
                if (!entry.getKey().equals("methodName")){
                    result.put(entry.getKey(),entry.getValue()[0]);
                }

            }
        }
        return result;
    }



    public static enum TradeStatus {
        WAIT_BUYER_PAY,TRADE_CLOSED,TRADE_SUCCESS,TRADE_FINISHED
    }
    public static class BizContent{
        //商户交易订单号
        private String out_trade_no;
        //销售产品码，与支付宝签约的产品码名称。
        //注：目前仅支持FAST_INSTANT_TRADE_PAY
        private String product_code="FAST_INSTANT_TRADE_PAY";
        //订单的总金额
        private double total_amount;
        //订单标题 必选
        private String subject;
        //	订单描述 可选
        private String body;

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getProduct_code() {
            return product_code;
        }

        public void setProduct_code(String product_code) {
            this.product_code = product_code;
        }

        public double getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(double total_amount) {
            this.total_amount = total_amount;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        private  BizContent(String out_trade_no, double total_amount, String subject) {
            this.out_trade_no = out_trade_no;
            this.total_amount = total_amount;
            this.subject = subject;
        }

    }


}
