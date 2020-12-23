package top.chris.shop.service.impl;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.chris.shop.exception.UserException;
import top.chris.shop.mapper.UsersMapper;
import top.chris.shop.pojo.Users;
import top.chris.shop.pojo.bo.UserInfoBo;
import top.chris.shop.pojo.bo.UsersBo;
import top.chris.shop.pojo.vo.UserInfoVo;
import top.chris.shop.service.MyCenterService;

import java.util.Date;

@Log
@Service
public class MyCenterServiceImpl implements MyCenterService {
    @Autowired
    private UsersMapper usersMapper;

    /**
     * 根据userId获取用户信息
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserInfoVo queryUserInfoByUserId(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        if (users == null){
            throw new UserException("在数据库中未找到指定UserID为:"+userId+"的信息");
        }
        UserInfoVo vo = new UserInfoVo();
        vo.setBirthday(users.getBirthday());
        vo.setEmail(users.getEmail());
        vo.setMobile(users.getMobile());
        vo.setNickname(users.getNickname());
        vo.setRealname(users.getRealname());
        vo.setSex(users.getSex());
        vo.setFaceUrl(users.getFace());
        return vo;
    }

    /**
     * 根据用户id修改用户个人信息
     * @param userId 用户id
     * @return
     */
    @Override
    public Integer updateUserInfoByUserId(UserInfoBo bo, String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        if (users == null){
            throw new UserException("在数据库中未找到指定UserID为:"+userId+"的信息");
        }
        log.info("修改前的UserInfo："+ ReflectionToStringBuilder.toString(users));
        if (bo.getNickname() != null ){
            users.setNickname(bo.getNickname());
        }
        if (bo.getEmail() != null ){
            users.setEmail(bo.getEmail());
        }
        if (bo.getMobile() != null ){
            log.info("最新的电话号码："+bo.getMobile());
            users.setMobile(bo.getMobile());
        }
        if (bo.getRealname() != null){
            users.setRealname(bo.getRealname());
        }
        if (bo.getBirthday() != null){
            users.setBirthday(bo.getBirthday());
        }
        //修改更新时间
        users.setCreatedTime(new Date());
        log.info("修改后的UserInfo："+ ReflectionToStringBuilder.toString(users));
        Integer result = usersMapper.updateByPrimaryKey(users);
        return result;
    }

    @Override
    public Integer updateUserInfoOfFaceByUserId(String userId, String picAddressUrl) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        if (users == null){
            throw new UserException("在数据库中未找到指定UserID为:"+userId+"的信息");
        }
        users.setFace(picAddressUrl);
        return usersMapper.updateByPrimaryKey(users);
    }


}
