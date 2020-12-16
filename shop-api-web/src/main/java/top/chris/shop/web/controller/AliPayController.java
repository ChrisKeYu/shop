package top.chris.shop.web.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
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
    public JsonResult callBack(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AlipayApiException {
        return JsonResult.isOk(payService.callBackAndUpdateOrderStatus(httpRequest,httpResponse));
    }
}
