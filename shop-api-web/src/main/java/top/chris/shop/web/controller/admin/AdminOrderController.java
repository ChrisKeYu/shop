package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.service.admin.AdminOrderService;
import top.chris.shop.util.JsonResult;

@Log
@Api("管理员订单管理器")
@RestController
@RequestMapping("/adminOrder")
public class AdminOrderController {
    @Autowired
    private AdminOrderService service;

    @ApiOperation("根据条件查询所有的订单信息")
    @GetMapping("/orderItemInfo")
    public JsonResult renderAllOrderItemInfo(String condition,@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("前端传入的参数为："+condition);
        return JsonResult.isOk(service.queryOrderItemInfoByCondition(condition,page, pageSize));
    }

    @ApiOperation("根据条件查询所有的订单信息")
    @GetMapping("/search")
    public JsonResult renderOrderItemInfoById(String id,@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("前端传入的ID参数为："+id);
        return JsonResult.isOk(service.queryOrderItemInfoById(id,page, pageSize));
    }

    @ApiOperation("根据订单id修改订单状态")
    @GetMapping("/delivery")
    public JsonResult deliveryOrderById(String id,String status){
        log.info("前端传入的ID参数为："+id +":"+status);
        service.updataOrderStatusById(id,status);
        return JsonResult.isOk();
    }

    @ApiOperation("根据订单id删除订单状态")
    @GetMapping("/del")
    public JsonResult delOrderById(String id){
        log.info("前端传入的ID参数为："+id );
        service.delOrderById(id);
        return JsonResult.isOk();
    }

}
