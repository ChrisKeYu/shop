package top.chris.shop.web.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.pojo.bo.ItemsCommentsBo;
import top.chris.shop.service.MyCommentService;
import top.chris.shop.util.JsonResult;

import java.util.List;

@Log
@Api("个人中心的订单评论控制器，管理订单评论")
@RestController
@RequestMapping("/mycomments")
public class MyCommentsController {
    @Autowired
    private MyCommentService myCommentService;

    @ApiOperation("查询指定用户订单状态的评论数")
    @PostMapping("/pending")
    public JsonResult getOrderComment(String userId,String orderId){
        return JsonResult.isOk(myCommentService.queryOrderCommentByUserIdAndOrderId(userId,orderId));
    }

    @ApiOperation("写入商品的评论到数据库中")
    @PostMapping("/saveList")
    public JsonResult writeOrderItemsComments(String userId, String orderId,@RequestBody List<ItemsCommentsBo> bo){
        Integer integer = myCommentService.updateOrderCommentByCommentId(orderId,bo);
        log.info("修改了多少条数据:"+integer);
        return JsonResult.isOk(integer);
    }
}
