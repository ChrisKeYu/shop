package top.chris.shop.service;

import top.chris.shop.util.JsonResult;
import top.chris.shop.pojo.bo.UsersBo;
import top.chris.shop.pojo.vo.UsersVo;

public interface PassportService {
    //检测用户名是否在数据中已经存在
    JsonResult usernameIsExist(String username);
    //注册方法
    UsersVo regist(UsersBo usersBo);
    //登录方法
    UsersVo login(UsersBo usersBo);
}
