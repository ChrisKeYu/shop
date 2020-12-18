package top.chris.shop.web.controller;

import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.pojo.bo.CatItemsBo;
import top.chris.shop.pojo.bo.CommentBo;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.service.ItemsService;
import top.chris.shop.util.JsonResult;

import javax.swing.*;
import java.util.List;
import java.util.jar.JarEntry;

@Api("商品控制器，管理商品")
@RestController
@RequestMapping("/items")
public class ItemsController {
    @Autowired
    private ItemsService itemsService;

    @ApiOperation("搜索某类下面的具体商品后的接口")
    @GetMapping("/catItems")
    public JsonResult catItems(SearchItemsBo itemsBo){
        return JsonResult.isOk(itemsService.catItems(itemsBo));
    }

    @ApiOperation("搜索框接口")
    @GetMapping("/search")
    public JsonResult searchItems(SearchItemsBo itemsBo){
        return JsonResult.isOk(itemsService.searchItemsLikeName(itemsBo));
    }

    @ApiOperation("商品详情接口")
    @GetMapping("/info/{itemId}")
    public JsonResult renderItemPageInfo(@PathVariable(required = true) String itemId){
        return JsonResult.isOk(itemsService.queryItemPageInfo(itemId));
    }

    @ApiOperation("统计商品评论各等价数量接口")
    @GetMapping("/commentLevel")
    public JsonResult renderCommentLevel(String itemId){
        return JsonResult.isOk(itemsService.renderCommentLevelyItemId(itemId));
    }

    @ApiOperation("商品各个类别评论和所有评论查询接口(如果前端没有传入分页参数，给默认值，传入字符串自动转换整形)")
    @GetMapping("/comments")
    public JsonResult renderCommentContent(CommentBo commentBo, @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(itemsService.renderCommentByItemIdAndLevel(commentBo,page,pageSize));
    }



}



/*//http://localhost:8080/items/refresh?itemSpecIds=cake-1004-spec-1,cake-1004-spec-2,cake-1004-spec-3
    //specId itemId itemImgUrl itemName specName priceNormal priceDiscount buyCounts
    //  购物车暂时没有持久化到数据库中 （TODO）
    @ApiOperation("输出到购物车内商品信息")
    @GetMapping("/refresh")
    public JsonResult renderShopCart(String[] itemSpecIds){
        return JsonResult.isOk(itemsService.renderShopCart(itemSpecIds));
    }*/