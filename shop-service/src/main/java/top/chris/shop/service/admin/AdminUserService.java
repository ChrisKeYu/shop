package top.chris.shop.service.admin;

import top.chris.shop.pojo.vo.adminVo.AllUserInfoVo;

import java.util.List;

public interface AdminUserService {
    //管理员查询所有用户信息
    List<AllUserInfoVo> queryAllUserInfo ();
}
