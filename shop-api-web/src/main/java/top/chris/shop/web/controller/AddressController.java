package top.chris.shop.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.exception.AddressException;
import top.chris.shop.pojo.bo.AddressBo;
import top.chris.shop.service.AddressService;
import top.chris.shop.util.JsonResult;

@RestController()
@RequestMapping("/address")
public class AddressController {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

    @PostMapping("/add")
    public JsonResult add(@RequestBody AddressBo addressBo){ //如果前端传入的数据是放在Request Payload中，那么要使用RequestBody接受，不能使用RequestParam
        log.info(ReflectionToStringBuilder.toString(addressBo));
        addressService.addUserAddress(addressBo);
        //TODO 如果出错就抛异常，交给统一异常处理机制处理，控制层只需要返回ok，不同异常，设置不同异常通知处理
        return JsonResult.isOk();
    }

    //TODO 以后有了权限框架后，设置只能够查询自己的地址列表
    @PostMapping("/list")
    public JsonResult list(String userId){
        return JsonResult.isOk(addressService.renderUserAddress(userId));
    }

    @PostMapping("/update")
    public JsonResult update(@RequestBody AddressBo addressBo){
        log.info(ReflectionToStringBuilder.toString(addressBo));
        if (StringUtils.isEmpty(addressBo.getAddressId())){
            throw new AddressException("用户传过来的请求修改地址为空");
        }
        //TODO 如果出错就抛异常，交给统一异常处理机制处理，控制层只需要返回ok，不同异常，设置不同异常通知处理
        return JsonResult.isOk(addressService.updateUserAddress(addressBo));
    }
    @PostMapping("/setDefalut")
    public JsonResult setDefalut(@RequestParam(required = true) String userId,@RequestParam(required = true) String addressId){
        //TODO 如果出错就抛异常，交给统一异常处理机制处理，控制层只需要返回ok，不同异常，设置不同异常通知处理
        return JsonResult.isOk(addressService.updateDefalutUserAddress(userId,addressId));
    }

    @PostMapping("/delete")
    public JsonResult delete(@RequestParam(required = true) String userId,@RequestParam(required = true) String addressId){
        //TODO 如果出错就抛异常，交给统一异常处理机制处理，控制层只需要返回ok，不同异常，设置不同异常通知处理
        return JsonResult.isOk(addressService.deleteUserAddress(userId,addressId));
    }


}
