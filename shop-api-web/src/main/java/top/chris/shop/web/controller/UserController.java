package top.chris.shop.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.service.admin.AdminUserService;

import top.chris.shop.util.JsonResult;


@Api("用户管理接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AdminUserService adminUserService;

    @Transactional(propagation = Propagation.REQUIRED)
    @ApiOperation("查询所有用户")
    @GetMapping("/findAll")
    public JsonResult findAllUser(){
        return JsonResult.isOk(adminUserService.queryAllUserInfo());
    }
}
