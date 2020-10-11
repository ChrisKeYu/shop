package top.chris.shop.service;

import top.chris.shop.util.JsonResult;
import top.chris.shop.pojo.bo.UsersBo;
import top.chris.shop.pojo.vo.UsersVo;

public interface PassportService {

    JsonResult usernameIsExist(String username);

    UsersVo regist(UsersBo usersBo);

    UsersVo login(UsersBo usersBo);
}
