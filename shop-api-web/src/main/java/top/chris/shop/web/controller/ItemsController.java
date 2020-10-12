package top.chris.shop.web.controller;

import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.pojo.bo.CatItemsBo;
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
    
    @GetMapping("/info/{itemId}")
    public JsonResult renderItemPageInfo(@PathVariable(required = true) String itemId){
        System.out.println("商品详情的ID："+itemId);
        return JsonResult.isOk(itemsService.queryItemPageInfo(itemId));
    }

    @GetMapping("/commentLevel")
    public JsonResult renderCommentLevel(String itemId){
        System.out.println("商品详情的ID："+itemId);
        return JsonResult.isOk(itemsService.renderCommentLevel(itemId));
    }
}
