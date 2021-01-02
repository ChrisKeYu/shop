package top.chris.shop.web.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.service.admin.AdminItemCommentService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

@Api("管理员管理商品评论的接口")
@Log
@RestController()
@RequestMapping("/adminItemComment")
public class AdminItemCommentController {
    @Autowired
    private AdminItemCommentService service;

    @ApiOperation("按条件查询所有的商品评论信息")
    @GetMapping("/itemCommentInfo")
    public JsonResult renderAllItemCommentInfo(String condition,@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询条件："+condition);
        return JsonResult.isOk(service.queryAllItemComment(condition,page, pageSize));
    }

    @ApiOperation("多条件查询所有的商品规格信息")
    @PostMapping("/search")
    public JsonResult renderItemCommentInfoByCondition(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        PagedGridResult pagedGridResult = service.queryItemCommentInfoByCondition(bo, page, pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件，请选择任何一个条件进行查询！");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }
    @ApiOperation("删除商品参数信息")
    @GetMapping("/delComment")
    public JsonResult delParamByItemId(String id){
        String s = service.deleteItemCommentInfoBySpecId(id);
        if (s == null){
            return JsonResult.isErr(500,"该评论已经被删除了");
        }
        return JsonResult.isOk();
    }


}
