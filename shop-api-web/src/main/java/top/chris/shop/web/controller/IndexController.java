package top.chris.shop.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.service.IndexService;
import top.chris.shop.util.JsonResult;

@Api("首页商品信息控制器，管理首页商品")
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @ApiOperation("轮播图接口")
    @GetMapping("/carousel")
    public JsonResult rendersCarousel(){
        return JsonResult.isOk(indexService.rendersCarousel());
    }

    @ApiOperation("一级菜单栏接口")
    @GetMapping("/cats")
    public JsonResult rendersCats(){
        return JsonResult.isOk(indexService.rendersCats());
    }

    @ApiOperation("二三级菜单栏接口")
    @GetMapping("/subCat/{rootCatId}")
    public JsonResult rendersSubCat(@PathVariable Integer rootCatId){ //请求url中携带的参数需要用@PathVariable标注，而且在@GetMapping中也要用花括号括起来，写入前端传入的参数.
        System.out.println("选择的一级菜单id号码:"+rootCatId);
        return JsonResult.isOk(indexService.rendersSubCats(rootCatId));
    }

    @ApiOperation("二级菜单栏接口")
    @GetMapping("/allSubCat")
    public JsonResult rendersAllSubCat(){ //请求url中携带的参数需要用@PathVariable标注，而且在@GetMapping中也要用花括号括起来，写入前端传入的参数.
        return JsonResult.isOk(indexService.rendersAllSubCats());
    }

    @ApiOperation("首页每一大类对应具体商品展览的接口")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JsonResult rendersSubItemsCats(@PathVariable Integer rootCatId){ //请求url中携带的参数需要用@PathVariable标注，而且在@GetMapping中也要用花括号括起来，写入前端传入的参数.
        System.out.println("选择的一级菜单id号码:"+rootCatId);
        return JsonResult.isOk(indexService.rendersSubItemsCats(rootCatId));
    }
}
