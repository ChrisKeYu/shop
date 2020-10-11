package top.chris.shop.service.impl;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.common.ShopConstant;
import top.chris.shop.enums.Sex;
import top.chris.shop.util.DateUtil;
import top.chris.shop.util.JsonResult;
import top.chris.shop.mapper.UsersMapper;
import top.chris.shop.pojo.Users;
import top.chris.shop.pojo.bo.UsersBo;
import top.chris.shop.pojo.vo.UsersVo;
import top.chris.shop.service.PassportService;

import java.util.Date;
import java.util.List;

@Service
public class PassportServiceImpl implements PassportService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public JsonResult usernameIsExist(String username) {
        Users users = new Users();
        users.setUsername(username);
        //通用插件，会根据传入的Users对象中，不为空的值作为条件去查询。如果查询数据为0，那么userList被赋值为null
        List<Users> userList = usersMapper.select(users);
        if (userList.size() > 0){
            return JsonResult.isErr(500,"用户名:"+username+"已存在,请重新输入用户名");
        }else {
            return JsonResult.ok();
        }
    }

    @Override
    public UsersVo regist(UsersBo usersBo) {
        Users users = new Users(sid.next(),usersBo.getUsername(),passwordEncoder.encode(usersBo.getPassword()),
                usersBo.getUsername(),usersBo.getUsername(), ShopConstant.defaultUserFaceImage,null,null,
                Sex.SECRET.type,DateUtil.stringToDate("1999-1-1"),new Date(),new Date());
        usersMapper.insert(users);
        UsersVo usersVo = new UsersVo(users.getId(),users.getUsername(),users.getFace(),users.getSex());
        return usersVo;
    }

    @Override
    public UsersVo login(UsersBo usersBo) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",usersBo.getUsername());
        Users users = usersMapper.selectOneByExample(example);
        UsersVo usersVo = null;
        if (users == null && passwordEncoder.matches(usersBo.getPassword(),users.getPassword())){ //检查账号和密码是否匹配
            return usersVo;
        }
        usersVo = new UsersVo(users.getId(),users.getUsername(),users.getFace(),users.getSex());
        return usersVo;
    }


}