package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.AdminCategoryBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.service.admin.AdminCarouselService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

@Api("管理员管理轮播图的接口")
@Log
@RestController()
@RequestMapping("/adminCarousel")
public class AdminCarouselController {
    @Autowired
    private AdminCarouselService service;

    @ApiOperation("查询所有的轮播图信息")
    @GetMapping("/CarouselInfo")
    public JsonResult renderAllCategorysInfo(String condition , @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("前端传入的参数:"+condition);
        PagedGridResult pagedGridResult = service.queryAllCarousel(condition, page, pageSize);
        if (pagedGridResult == null){
            return JsonResult.isErr(500,"查询结果为0条数据！");
        }else {
            return JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("多条件查询所有的轮播图信息")
    @PostMapping("/search")
    public JsonResult renderCategorysInfoByCondition(@RequestBody  AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        PagedGridResult pagedGridResult = service.queryCarouselInfoByCondition(bo,page,pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件或者只输入了一级分类，请输入分级条件的完整查询条件");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("上传新的轮播图及轮播信息")
    @PostMapping("/addCarouselPic")
    public JsonResult addCategorysImg(AdminCategoryBo bo, @RequestParam("file") MultipartFile file){
        log.info("查询前端传入的图片附加参数:"+ bo+ReflectionToStringBuilder.toString(file));
        String s = service.addNewCarouselInfo(bo, file);
        log.info("结果为："+s);
        if (s.contains("success")){
            return JsonResult.isOk();
        }else {
            return JsonResult.isErr(500,s);
        }
    }

    @ApiOperation("根据轮播图的ID更改轮播图信息")
    @PostMapping("/updateCarousel")
    public JsonResult changeCarouselInfoById(@RequestBody AdminCategoryBo bo){
        log.info("查询前端传入的图片id参数:"+ReflectionToStringBuilder.toString(bo));
        service.updateCarouselInfoById(bo);
        return JsonResult.isOk();
    }

    @ApiOperation("根据轮播图的ID更改掉原图")
    @PostMapping("/updateCarouselPic")
    public JsonResult changeCarouselPicById(String id,@RequestParam("file") MultipartFile file){
        log.info("查询前端传入的图片id参数:"+id+ReflectionToStringBuilder.toString(file));
        service.updateCarouselPicById(id,file);
        return JsonResult.isOk();
    }

    @ApiOperation("删除轮播图信息-只做不展示处理")
    @GetMapping("/delCarousel")
    public JsonResult delParamByItemId(String id){
        service.deleteCarouselById(id);
        return JsonResult.isOk();
    }

    @ApiOperation("获取所有Item信息")
    @GetMapping("/allItemInfo")
    public JsonResult getAllItemInfo(){
        return JsonResult.isOk(service.queryAllItemInfo());
    }


    @ApiOperation("根据轮播图Id获取轮播图信息")
    @GetMapping("/getCarouselById")
    public JsonResult getCarouselById(String id){
        return JsonResult.isOk(service.queryCarouselInfoById(id));
    }
}
