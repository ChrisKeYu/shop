package top.chris.shop.web.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.common.AlipayConfig;
import top.chris.shop.service.PayService;
import top.chris.shop.util.JsonResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Log
@RestController
@RequestMapping("/payment")
public class AliPayController {


    @Autowired
    private PayService payService;

    @ApiOperation("支付宝支付")
    @PostMapping("/getALiPayPayCode")
    public JsonResult topay(String merchantUserId,String merchantOrderId) {
        return JsonResult.isOk(payService.toPay(merchantUserId,merchantOrderId));
    }

    @ApiOperation("request接受支付宝返回的支付结果以及处理订单支付后的业务逻辑")
    @GetMapping("/callback")
    public JsonResult callBack(HttpServletRequest request, HttpServletResponse response){
        //TODO 处理订单支付后的业务逻辑：修改订单状态：未支付=》已支付等等
        final Map<String, String[]> parameterMap = request.getParameterMap();
//        String out_trade_no = request.getParameter("out_trade_no");//支付宝返回订单号
//        String trade_status = request.getParameter("trade_status");//交易状态
//        String total_amount = request.getParameter("total_amount");//交易的订单金额
        String subject = request.getParameter("subject");//订单标题
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            for (Map.Entry<String, String[]> stringEntry : entries) {
                System.out.println(stringEntry.getKey()+"----"+stringEntry.getValue()[0]);
            }
        }

        log.info("支付结果已知到");
        return JsonResult.isOk();
    }
}
