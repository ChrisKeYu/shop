package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.AdminCarouselBo;
import top.chris.shop.pojo.bo.adminBo.AdminCategoryBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.service.admin.AdminCategoryService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

@Api("管理员管理商品分类的接口")
@Log
@RestController()
@RequestMapping("/adminCategory")
public class AdminCategoryController {
    @Autowired
    private AdminCategoryService service;


    @ApiOperation("查询所有的分级信息")
    @GetMapping("/categoryInfo")
    public JsonResult renderAllcategoryInfo(String condition , @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("前端传入的参数:"+condition);
        PagedGridResult pagedGridResult = service.queryAllCategoryInfo(condition, page, pageSize);
        if (pagedGridResult == null){
            return JsonResult.isErr(500,"查询结果为0条数据！");
        }else {
            return JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("多条件查询所有的轮播图信息")
    @PostMapping("/search")
    public JsonResult renderCategorysInfoByCondition(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        PagedGridResult pagedGridResult = service.queryCategoryInfoByCondition(bo,page,pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件，请输入完整查询条件");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("上传新的图片及分级信息")
    @PostMapping("/addCategoryPic")
    public JsonResult addCategorysImgAndInfo(AdminCategoryBo bo, @RequestParam("file") MultipartFile file){
        log.info("查询前端传入的图片附加参数:"+ bo+ReflectionToStringBuilder.toString(file));
//        String s = service.addNewCarouselInfo(bo, file);
//        log.info("结果为："+s);
//        if (s.contains("success")){
//            return JsonResult.isOk();
//        }else {
//            return JsonResult.isErr(500,s);
//        }
        return  JsonResult.isOk();
    }

    @ApiOperation("上传新的分级信息")
    @PostMapping("/addCarousel")
    public JsonResult addCategorysInfo(@RequestBody AdminCategoryBo bo){
        log.info("查询前端传入的参数:"+ bo);
//        String s = service.addNewCarouselInfo(bo, file);
//        log.info("结果为："+s);
//        if (s.contains("success")){
//            return JsonResult.isOk();
//        }else {
//            return JsonResult.isErr(500,s);
//        }
        return  JsonResult.isOk();
    }

    @ApiOperation("根据Id获取分类信息")
    @GetMapping("/queryCategoryById")
    public JsonResult getCategorysInfoById(String id){
        log.info("查询前端传入的参数:"+ id);
        return  JsonResult.isOk(service.queryCategoryInfoById(id));
    }

}
