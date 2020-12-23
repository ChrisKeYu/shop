package top.chris.shop.service;

import top.chris.shop.pojo.bo.UserInfoBo;
import top.chris.shop.pojo.vo.UserInfoVo;

public interface MyCenterService {
    //根据userId获取用户信息
    UserInfoVo queryUserInfoByUserId(String userId);
    //根据用户id修改用户个人信息
    Integer updateUserInfoByUserId(UserInfoBo bo, String userId);
    //上传指定用户id的头像照片地址到数据库中
    Integer updateUserInfoOfFaceByUserId(String userId,String picAddressUrl);
}
