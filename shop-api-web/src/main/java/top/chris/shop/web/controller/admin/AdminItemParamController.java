package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.pojo.bo.adminBo.AdminItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.service.admin.AdminItemParamService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

@Api("管理员管理商品参数的接口")
@Log
@RestController()
@RequestMapping("/adminItemParam")
public class AdminItemParamController {
    @Autowired
    private  AdminItemParamService  paramService;

    @ApiOperation("多条件查询所有的商品信息")
    @PostMapping("/search")
    public JsonResult renderItemsInfoByCondition(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        PagedGridResult pagedGridResult = paramService.queryItemParamInfoByCondition(bo,page,pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件，请选择任何一个条件进行查询！");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("查询所有的商品参数信息")
    @GetMapping("/itemParamInfo")
    public JsonResult renderAllItemsInfo( @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return JsonResult.isOk(paramService.queryAllItemParam(page, pageSize));
    }

    @ApiOperation("根据paramId查询指定商品参数信息")
    @GetMapping("/searchItemParamInfo")
    public JsonResult renderItemsInfoByItemId(String paramId){
        return JsonResult.isOk(paramService.queryItemParamByItemId(paramId));
    }

    @ApiOperation("更新商品参数信息")
    @PostMapping("/updateParam")
    public JsonResult updateParamByItemId(@RequestBody AdminItemParamBo bo){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString("bo"));
        paramService.updateItemParamById(bo);
        return JsonResult.isOk();
    }

    @ApiOperation("删除商品参数信息")
    @GetMapping("/delParam")
    public JsonResult delParamByItemId(String id){
        String s = paramService.delItemParamById(id);
        if (s.equals("success")){
            return JsonResult.isOk();
        }else {
            return JsonResult.isErr(500,s);
        }

    }


}
