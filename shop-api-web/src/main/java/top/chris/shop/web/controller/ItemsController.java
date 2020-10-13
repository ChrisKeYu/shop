package top.chris.shop.web.controller;

import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import io.swagger.models.auth.In;
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

import java.util.List;
import java.util.jar.JarEntry;


@RestController
@RequestMapping("/items")
public class ItemsController {
    @Autowired
    private ItemsService itemsService;

    //搜索某类下面的具体商品后的接口
    @GetMapping("/catItems")
    public JsonResult catItems(SearchItemsBo itemsBo){
        return JsonResult.isOk(itemsService.catItems(itemsBo));
    }

    //搜索框接口
    @GetMapping("/search")
    public JsonResult searchItems(SearchItemsBo itemsBo){
        return JsonResult.isOk(itemsService.searchItemsLikeName(itemsBo));
    }
    //商品详情接口
    @GetMapping("/info/{itemId}")
    public JsonResult renderItemPageInfo(@PathVariable(required = true) String itemId){
        return JsonResult.isOk(itemsService.queryItemPageInfo(itemId));
    }
    //统计商品评论各等价数量接口
    @GetMapping("/commentLevel")
    public JsonResult renderCommentLevel(String itemId){
        return JsonResult.isOk(itemsService.renderCommentLevelyItemId(itemId));
    }

    //商品各个类别评论和所有评论查询接口(如果前端没有传入分页参数，给默认值，传入字符串自动转换整形)
    @GetMapping("/comments")
    public JsonResult renderCommentContent(CommentBo commentBo, @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(itemsService.renderCommentByItemIdAndLevel(commentBo,page,pageSize));
    }

}
