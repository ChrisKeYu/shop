package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.pojo.bo.adminBo.AdminItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminUserAddressBo;
import top.chris.shop.service.admin.AdminUserAddressService;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.PagedGridResult;

@Log
@Api("管理员用户收货地址管理器")
@RestController
@RequestMapping("/adminAddress")
public class AdminUserAddressController {
    @Autowired
    private AdminUserAddressService addressService;

    @Transactional(propagation = Propagation.REQUIRED)
    @ApiOperation("多条件查询所有用户")
    @GetMapping("/findAll")
    public JsonResult findAllUserAddress(String condition, @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("前端传入的参数:"+condition);
        PagedGridResult pagedGridResult = addressService.queryAllUserAddressInfo(condition, page, pageSize);
        if (pagedGridResult == null){
            return JsonResult.isErr(500,"该用户暂时还有没填入收货地址");
        }
        return JsonResult.isOk(pagedGridResult);
    }
    @ApiOperation("根据AddressId查询指定地址信息")
    @GetMapping("/getUserAddressById")
    public JsonResult renderItemsInfoByItemId(String addressId){
        return JsonResult.isOk(addressService.queryUserAddressInfo(addressId));
    }

    @ApiOperation("更新用户收货地址信息")
    @PostMapping("/updateAddress")
    public JsonResult updateAddressById(@RequestBody AdminUserAddressBo bo){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString("bo"));
        addressService.updateUserAddressInfoById(bo);
        return JsonResult.isOk();
    }

    @ApiOperation("添加用户收货地址信息")
    @PostMapping("/addAddress")
    public JsonResult addAddress(@RequestBody AdminUserAddressBo bo){
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString("bo"));
        addressService.addUserAddressInfoById(bo);
        return JsonResult.isOk();
    }

    @ApiOperation("删除商品参数信息")
    @GetMapping("/delAddress")
    public JsonResult delAddressByItemId(String id){
        addressService.delAddressByItemId(id);
       return JsonResult.isOk();
    }

}
