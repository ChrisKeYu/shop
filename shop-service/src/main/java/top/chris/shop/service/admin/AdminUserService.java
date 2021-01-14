package top.chris.shop.service.admin;

import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.RequestParam;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminUserParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminUserInfoVo;
import top.chris.shop.pojo.vo.adminVo.AllUserInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface AdminUserService {
    //管理员查询所有用户信息
    PagedGridResult queryAllUserInfo (String conditionm,Integer page, Integer pageSize);

    //根据用户id查询指定用户的信息
    AdminUserInfoVo queryUserInfoById(String userId);

    //根据用户id修改指定用户的信息
    Map<String, String> updateUserInfoById(AdminUserParamBo bo) throws ParseException;

    //添加新的用户
    Map<String, String> addUserInfo(AdminUserParamBo bo) throws ParseException;

    //根据用户名查询用户信息
    PagedGridResult queryUserInfoByUserName(String userName,Integer page, Integer pageSize);

    //根据用户userId删除用户
    String delUserByUserId(String userId);

    //重置用户密码
    Integer updateUserPwdById(String id);

    //修改用户密码
    Integer updateNewUserPwdById(String id,String pwd);

}
