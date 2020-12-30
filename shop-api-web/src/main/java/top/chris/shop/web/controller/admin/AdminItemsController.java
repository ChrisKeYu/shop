package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.*;
import top.chris.shop.pojo.vo.adminVo.AdminItemImgsVo;
import top.chris.shop.service.admin.AdminItemsService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

import java.util.List;
import java.util.Map;

@Api("管理员管理商品的接口")
@Log
@RestController()
@RequestMapping("/adminItem")
public class AdminItemsController {
    @Autowired
    AdminItemsService adminItemsService;

    @ApiOperation("多条件查询所有的商品信息")
    @GetMapping("/itemsInfo")
    public JsonResult renderAllItemsInfo(String condition, @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(adminItemsService.queryAllItemsByCondition(condition, page, pageSize));
    }

    @ApiOperation("根据ItemId添加商品参数")
    @PostMapping("/addParam")
    public JsonResult addParam(@RequestBody AdminItemParamBo params){
        log.info("前端接受的参数为："+ ReflectionToStringBuilder.toString(params));
        Integer result = adminItemsService.insertItemParamByItemId(params);
        if (result != 0){
            log.info("success");
            return JsonResult.isOk("添加成功");
        }else {
            log.info("添加失败");
            return JsonResult.isErr(500,"商品参数的数据表中已经存在了外键ItemId："+params.getItemId()+"的数据了，不能重复添加");
        }

    }

    @ApiOperation("根据ItemId添加商品参数")
    @PostMapping("/addItem")
    public JsonResult addItem(@RequestBody AdminItemBo bo){
        log.info("前端接受的参数为："+ ReflectionToStringBuilder.toString(bo));
        Integer result = adminItemsService.insertItem(bo);
        if (result == 500){
            return JsonResult.isErr(500,"商品名字重复");
        }else {
            return JsonResult.isOk();
        }
    }

    @ApiOperation("多条件搜索指定商品信息")
    @PostMapping("/search")
    public JsonResult searchItemsInfo(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize){
        PagedGridResult pagedGridResult = adminItemsService.queryItemsByCondition(bo, page, pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件，请选择任何一个条件进行查询！");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("根据ItemId添加商品规格")
    @PostMapping("/addSpec")
    public JsonResult addSpec(@RequestBody AdminItemSpecBo bo){
        log.info("前端接受的参数为："+ ReflectionToStringBuilder.toString(bo));
        Map<Integer, String> map = adminItemsService.insertItemSpec(bo);
        if (map.containsKey(0)){
            return JsonResult.isErr(500,map.get(0));
        }else {
            return JsonResult.isOk();
        }
    }

    @ApiOperation("根据ItemId上传商品照片接口")
    @PostMapping("/upload")
    public JsonResult addImgs(String id,@RequestParam("file") List<MultipartFile> files){
        log.info("前端接受的参数为："+ id);
        log.info("前端接受的参数为："+ ReflectionToStringBuilder.toString(files.size()));
        adminItemsService.insertItemImgs(id, files);
        return JsonResult.isOk();
    }

    @ApiOperation("根据ItemId查询商品照片")
    @GetMapping("/showImgs")
    public JsonResult renderItemImgs(String itemId){
        List<AdminItemImgsVo> vos = adminItemsService.queryItemImgsByItemId(itemId);
        return JsonResult.isOk(vos);
    }

    @ApiOperation("根据ItemId查询商品照片")
    @GetMapping("/searchItemInfo")
    public JsonResult searchItemInfo(String itemId){
        return JsonResult.isOk(adminItemsService.queryItemInfoByItemId(itemId));
    }

    @ApiOperation("根据ItemId修改商品参数")
    @PostMapping("/updateItem")
    public JsonResult updateItem(@RequestBody AdminItemBo bo){
        log.info("前端接受的参数为："+ ReflectionToStringBuilder.toString(bo));
        return JsonResult.isOk(adminItemsService.updateItemInfo(bo));
    }


    @ApiOperation("根据ItemId添加商品参数")
    @GetMapping("/delItem")
    public JsonResult delItem(String itemId){
        Map<String, String> stringStringMap = adminItemsService.deleteItemByItemId(itemId);
        if (stringStringMap.containsKey("success")){
            return JsonResult.isOk(stringStringMap.get("success"));
        }else {
            return JsonResult.isErr(500,stringStringMap.get("fail"));

        }
    }

//    @ApiOperation("根据ItemId查询商品照片")
//    @PostMapping("/deleteImage")
//    public JsonResult deleteImage(String id,String directory,String deleteFile){
//        Integer integer = adminItemsService.deleteItemImgById(id, directory, deleteFile);
//        if (integer != 0){
//            return JsonResult.isOk();
//        }else {
//            return JsonResult.isErr(500,"删除Id为："+id+"的照片失败！");
//        }
//
//    }
}
