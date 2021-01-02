package top.chris.shop.web.controller.admin;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminUpdateImageBo;
import top.chris.shop.service.admin.AdminItemImageService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

@Api("管理员管理商品图片的接口")
@Log
@RestController()
@RequestMapping("/adminItemImage")
public class AdminItemImageController {
    @Autowired
    AdminItemImageService adminItemImageService;

    @ApiOperation("多条件查询所有的商品信息")
    @PostMapping("/imagesInfo")
    public JsonResult renderAllItemsInfo(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        return JsonResult.isOk(adminItemImageService.queryAllItemImagesInfoByCondition(bo,page,pageSize));
    }

    @ApiOperation("多条件搜索指定商品图片信息")
    @PostMapping("/search")
    public JsonResult searchItemsInfo(@RequestBody AdminSearchItemParamBo bo, @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        PagedGridResult pagedGridResult = adminItemImageService.queryItemImagesInfoByBo(bo,page,pageSize);
        if (pagedGridResult == null){
            return  JsonResult.isErr(500,"未输入查询条件，请选择任何一个条件进行查询！");
        }else if (pagedGridResult.getRows() == null){
            return  JsonResult.isErr(500,"查询结果为0，根据上市条件在数据库中查询不到数据!");
        }else {
            return  JsonResult.isOk(pagedGridResult);
        }
    }

    @ApiOperation("根据图片的ID更改掉原图")
    @PostMapping("/change")
    public JsonResult changeImgsByImageId(String imageId,@RequestParam("file") MultipartFile file){
        log.info("查询前端传入的图片id参数:"+ imageId);
        adminItemImageService.changeItemImgByImgId(imageId, file);
        return JsonResult.isOk();
    }

    @ApiOperation("根据图片的ID删除掉图片")
    @GetMapping("/delImg")
    public JsonResult delImgByImageId(String imageId){
        log.info("查询前端传入的图片id参数:"+ imageId);
        try {
            adminItemImageService.deleteImageByImageId(imageId);
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return JsonResult.isOk();
    }

//    @ApiOperation("搜索指定商品图片信息")
//    @GetMapping("/queryById")
//    public JsonResult searchItemsInfo(String imageId){
//        return JsonResult.isOk(adminItemImageService.queryImageInfoByImageId(imageId));
//    }

    @ApiOperation("多条件搜索指定商品图片信息")
    @PostMapping("/updateById")
    public JsonResult updateItemImageById(@RequestBody AdminUpdateImageBo bo){
        log.info("前端传入的信息："+ReflectionToStringBuilder.toString(bo));
        adminItemImageService.updateImageInfoByIdAndItemId(bo.getItemId(),bo.getOledStatus(),bo.getIsMain(),bo.getImageId());
        return JsonResult.isOk();
    }

}
