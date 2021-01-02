package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.pojo.bo.adminBo.AdminItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminItemSpecBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.service.admin.AdminItemSpecService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

import java.util.Map;

@Api("管理员管理商品参数的接口")
@Log
@RestController()
@RequestMapping("/adminItemSpec")
public class AdminItemSpecController {
    @Autowired
    private AdminItemSpecService specService;


    @ApiOperation("查询所有的商品规格信息")
    @GetMapping("/itemSpecInfo")
    public JsonResult renderAllItemsInfo(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(specService.queryAllItemSpec(page, pageSize));
    }

    @ApiOperation("多条件查询所有的商品规格信息")
    @PostMapping("/search")
    public JsonResult renderItemSpecInfoByCondition(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        PagedGridResult pagedGridResult = specService.queryItemSpecInfoByCondition(bo,page,pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件，请选择任何一个条件进行查询！");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("根据specId查询指定商品规格信息")
    @GetMapping("/searchItemSpecInfo")
    public JsonResult renderItemsInfoBySpecId(String specId){
        return JsonResult.isOk(specService.queryItemSpecInfoBySpecId(specId));
    }

    @ApiOperation("更新商品规格信息")
    @PostMapping("/updateSpec")
    public JsonResult updateSpecBySpecId(@RequestBody AdminItemSpecBo bo){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString("bo"));
        specService.updateItemSpecInfoBySpecId(bo);
        return JsonResult.isOk();
    }

    @ApiOperation("删除商品参数信息")
    @GetMapping("/delSpec")
    public JsonResult delParamByItemId(String id,String itemId){
        Map<String, String> stringStringMap = specService.deleteItemSpecInfoBySpecId(id,itemId);
        if (stringStringMap.containsKey("success")){
            return JsonResult.isOk(stringStringMap.get("success"));
        }else {
            return JsonResult.isErr(500,stringStringMap.get("fail"));

        }
    }


}
