package top.chris.shop.web.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.pojo.bo.adminBo.AdminUserParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminUserInfoVo;
import top.chris.shop.service.admin.AdminUserService;
import top.chris.shop.util.JsonResult;

import java.text.ParseException;
import java.util.Map;


@Log
@Api("管理员用户管理器")
@RestController
@RequestMapping("/adminUser")
public class AdminUserController {
    @Autowired
    private AdminUserService userService;

    @Transactional(propagation = Propagation.REQUIRED)
    @ApiOperation("多条件查询所有用户")
    @GetMapping("/findAll")
    public JsonResult findAllUser(String condition,@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("前端传入的参数:"+condition);
        return JsonResult.isOk(userService.queryAllUserInfo( condition,page, pageSize));
    }


    @ApiOperation("根据用户id查询指定用户的信息")
    @GetMapping("/getUserInfoById")
    public JsonResult getUserInfoById(String userId){
        AdminUserInfoVo adminUserInfoVo = userService.queryUserInfoById(userId);
        if (adminUserInfoVo == null){
            JsonResult.isErr(500,"在数据库中无法找到该用户");
        }
        return JsonResult.isOk(adminUserInfoVo);
    }

    @ApiOperation("添加用户信息")
    @PostMapping("/addUser")
    public JsonResult addUserByUserId(@RequestBody AdminUserParamBo bo) throws ParseException {
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        Map<String, String> map = userService.addUserInfo(bo);
        if (map.size() == 0){
            return JsonResult.isOk();
        }else {
            String result = "---";
            for (String s : map.keySet()) {
                if (s.equals("MobileIsExis")){
                    result += "--"+map.get(s);
                }
                if (s.equals("EmailIsExis")){
                    result += "--"+map.get(s);
                }
                if (s.equals("NicknameIsExis")){
                    result += "--"+map.get(s);
                }
                if (s.equals("UserNameIsExis")){
                    result += "--"+map.get(s);
                }
            }
            return JsonResult.isErr(500,result);
        }
    }


    @ApiOperation("更新用户信息")
    @PostMapping("/updateUser")
    public JsonResult updateUserByUserId(@RequestBody AdminUserParamBo bo) throws ParseException {
        log.info("查询前端传入的参数:"+ ReflectionToStringBuilder.toString(bo));
        Map<String, String> map = userService.updateUserInfoById(bo);
       if (map.size() == 0){
           return JsonResult.isOk();
       }else {
           String result = "---";
           for (String s : map.keySet()) {
               if (s.equals("MobileIsExis")){
                   result += "--"+map.get(s);
               }
               if (s.equals("EmailIsExis")){
                   result += "--"+map.get(s);
               }
               if (s.equals("NicknameIsExis")){
                   result += "--"+map.get(s);
               }
           }
           return JsonResult.isErr(500,result);
       }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @ApiOperation("根据用户名查询用户")
    @PostMapping("/search")
    public JsonResult search(@RequestBody String userName,@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info(userName);
        return JsonResult.isOk(userService.queryUserInfoByUserName(userName,page,pageSize));
    }

    @ApiOperation("删除商品参数信息")
    @GetMapping("/delUser")
    public JsonResult delParamByItemId(String id){
        String s = userService.delUserByUserId(id);
        if (s.equals("success")){
            return JsonResult.isOk();
        }else {
            return JsonResult.isErr(500,"该用户下还有订单未完成交易");
        }
    }

}
