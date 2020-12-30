package top.chris.shop.service.adminImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chris.shop.exception.UserException;
import top.chris.shop.mapper.UsersMapper;
import top.chris.shop.pojo.Users;
import top.chris.shop.pojo.vo.adminVo.AllUserInfoVo;
import top.chris.shop.service.admin.AdminUserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private UsersMapper usersMapper;



    @Override
    public List<AllUserInfoVo> queryAllUserInfo() {
        List<AllUserInfoVo> vos = new ArrayList<>();
        List<Users> users = usersMapper.selectAll();
        if (users.size() == 0){
            throw  new UserException("目前还没有注册用户");
        }
        for (Users user : users) {
            AllUserInfoVo vo = new AllUserInfoVo();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setRealname(user.getRealname());
            vo.setBirthday(user.getBirthday());
            vo.setCreatedTime(user.getCreatedTime());
            vo.setEmail(user.getEmail());
            vo.setFace(user.getFace());
            vo.setMobile(user.getMobile());
            vo.setSex(user.getSex());
            vo.setUpdatedTime(user.getUpdatedTime());
            vos.add(vo);
        }
        return vos;
    }
}
