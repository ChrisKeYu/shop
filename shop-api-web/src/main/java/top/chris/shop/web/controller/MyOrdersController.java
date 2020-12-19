package top.chris.shop.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.util.JsonResult;

@Api("个人中心的订单控制器，管理订单")
@RestController
@RequestMapping("/myorders")
public class MyOrdersController {

    @ApiOperation("查询指定用户订单")
    @PostMapping("/statusCounts")
    public JsonResult getOrderStatusCounts(String userId){

        return JsonResult.isOk();
    }
}
