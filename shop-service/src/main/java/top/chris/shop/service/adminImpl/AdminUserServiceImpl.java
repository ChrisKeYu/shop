package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.common.ShopConstant;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.enums.SexEnum;
import top.chris.shop.exception.UserException;
import top.chris.shop.mapper.UsersMapper;
import top.chris.shop.pojo.Users;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminUserParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;
import top.chris.shop.pojo.vo.adminVo.AdminUserInfoVo;
import top.chris.shop.pojo.vo.adminVo.AllUserInfoVo;
import top.chris.shop.pojo.vo.adminVo.UserOrderStatusVo;
import top.chris.shop.service.admin.AdminUserService;
import top.chris.shop.util.PagedGridResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private PageHelper pageHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public PagedGridResult queryAllUserInfo(String conditionm,Integer page, Integer pageSize) {
        if (conditionm == ""){
            pageHelper.startPage(page,pageSize);
            List<Users> users = usersMapper.selectAll();
            if (users.size() == 0){
                throw  new UserException("目前还没有注册用户");
            }
            List<AllUserInfoVo> vos = finfishAllUserInfoVo(users);
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else if (conditionm.equals("m")){
            pageHelper.startPage(page,pageSize);
            Example example = new Example(Users.class);
            example.createCriteria().andEqualTo("sex", 1);
            return finfishPagedGridResult(finfishAllUserInfoVo(usersMapper.selectByExample(example)),page);
        }else {
            pageHelper.startPage(page,pageSize);
            Example example = new Example(Users.class);
            example.createCriteria().andEqualTo("sex", 0);
            return finfishPagedGridResult(finfishAllUserInfoVo(usersMapper.selectByExample(example)),page);
        }

    }

    @Override
    public AdminUserInfoVo queryUserInfoById(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        if (users == null){
            return null;
        }
        AdminUserInfoVo vo = new AdminUserInfoVo();
        vo.setId(users.getId());
        vo.setRealname(users.getRealname());
        vo.setNickname(users.getNickname());
        vo.setSex(users.getSex());
        vo.setMobile(users.getMobile());
        vo.setEmail(users.getEmail());
        vo.setBirthday(users.getBirthday());
        return vo;
    }

    @Override
    public Map<String, String> updateUserInfoById(AdminUserParamBo bo) throws ParseException {
        Map<String, String> map = new HashMap<>();
        Users user = usersMapper.selectByPrimaryKey(bo.getId());
        if (user == null){
            return null;
        }
        user.setUpdatedTime(new Date());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        user.setBirthday(sf.parse(bo.getBirthday()));
        user.setSex(bo.getSex());
        user.setRealname(bo.getRealname());
        //检查数据是否被修改，如果没被修改不做任何操作
        if (!user.getMobile().equals(bo.getMobile())){
            Example example = new Example(Users.class);
            example.createCriteria().andEqualTo("mobile",bo.getMobile());
            List<Users> users1 = usersMapper.selectByExample(example);
            if (users1.size() != 0){
                //说明电话已存在，不能修改。
                map.put("MobileIsExis",bo.getMobile()+"的手机号码已存在，请重新输入");
            }else {
                user.setMobile(bo.getMobile());
            }
        }
        if (!user.getEmail().equals(bo.getEmail())){
            Example example = new Example(Users.class);
            example.createCriteria().andEqualTo("email",bo.getEmail());
            List<Users> users1 = usersMapper.selectByExample(example);
            if (users1.size() != 0){
                //说明邮箱已存在，不能修改。
                map.put("EmailIsExis",bo.getEmail()+"的邮箱已存在，请重新输入");
            }else {
                user.setEmail(bo.getEmail());
            }
        }
        if (!user.getNickname().equals(bo.getNickname())){
            Example example = new Example(Users.class);
            example.createCriteria().andEqualTo("nickname",bo.getNickname());
            List<Users> users1 = usersMapper.selectByExample(example);
            if (users1.size() != 0){
                //说明昵称已存在，不能修改。
                map.put("NicknameIsExis",bo.getNickname()+"的昵称已存在，请重新输入");
            }else {
                user.setNickname(bo.getNickname());
            }
        }
        usersMapper.updateByPrimaryKey(user);
        return map;
    }

    @Override
    public Map<String, String> addUserInfo(AdminUserParamBo bo) throws ParseException {
        Map<String, String> map = new HashMap<>();
        Users user = new Users();
        user.setId(sid.nextShort());
        user.setFace(ShopConstant.defaultUserFaceImage);//设置默认头像
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        user.setBirthday(sf.parse(bo.getBirthday()));
        user.setSex(bo.getSex());
        user.setRealname(bo.getRealname());
        user.setPassword(passwordEncoder.encode("123456"));//设置默认密码
        for (Users users : usersMapper.selectAll()) {
            //检查数据是否被修改，如果没被修改不做任何操作
            if (users.getUsername().equals(bo.getUserName())){
                //说明账户已存在，不能添加。
                map.put("UserNameIsExis",bo.getUserName()+"的用户名已存在，请重新输入");
            }else {
                user.setUsername(bo.getUserName());
            }
            if (users.getMobile().equals(bo.getMobile())){
                //说明电话已存在，不能添加。
                map.put("MobileIsExis",bo.getMobile()+"的手机号码已存在，请重新输入");
            }else {
                user.setMobile(bo.getMobile());
            }
            if (users.getEmail().equals(bo.getEmail())){
                //说明邮箱已存在，不能添加。
                map.put("EmailIsExis",bo.getEmail()+"的邮箱已存在，请重新输入");
            }else {
                user.setEmail(bo.getEmail());
            }
            if (users.getNickname().equals(bo.getNickname())){
                //说明昵称已存在，不能添加。
                map.put("NicknameIsExis",bo.getNickname()+"的昵称已存在，请重新输入");
            }else {
                user.setNickname(bo.getNickname());
            }
        }
        if (map.size() != 0){
            //添加操作失败
            return map;
        }else {
           usersMapper.insert(user);
           return map;
        }
    }

    @Override
    public PagedGridResult queryUserInfoByUserName(String userName,Integer page, Integer pageSize) {
        String[] split = userName.split(":");
        pageHelper.startPage(page,pageSize);
        Example example = new Example(Users.class);
        String value = "%" + split[1].split("\"")[1] + "%";
        example.createCriteria().andLike("username",value);
        List<Users> users = usersMapper.selectByExample(example);
        List<AllUserInfoVo> allUserInfoVos = finfishAllUserInfoVo(users);
        PagedGridResult pagedGridResult = finfishPagedGridResult(allUserInfoVos,page);
        return pagedGridResult;
    }

    @Override
    public String delUserByUserId(String userId) {
        Boolean flag = true;
        //查询该用户下的所有订单状态
        List<UserOrderStatusVo> vos = usersMapper.queryUserOrderStatusByUserId(userId);
        if (vos.size() != 0){ //该用户下的所有订单
            for (UserOrderStatusVo vo : vos) {
                if (vo.getOrderStatus() != OrdersStatusEnum.SUCCESS.type){
                    //只要该用户下的所有订单转台不是40：即交易未完成，那么都不可以删除该用户
                    flag = false;
                }
            }
            if (flag){
                //删除该用户
                usersMapper.deleteByPrimaryKey(userId);
                return "success";
            }else {
                return "fails";
            }
        }else {
            //该用户还没下单，所以可以删除。
            //删除该用户
            usersMapper.deleteByPrimaryKey(userId);
            return "success";
        }
    }

    /**
     * 用来封装查询好的用户信息
     * @param users
     * @return
     */
    public List<AllUserInfoVo> finfishAllUserInfoVo(List<Users> users){
        List<AllUserInfoVo> vos = new ArrayList<>();
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

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AllUserInfoVo> vos, Integer page){
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (vos != null || vos.size() != 0){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(vos);
            //插入查询到的指定数据，与上面传入的参数是不一致的，下面传入的参数是orders对象被pageHelper设定固定大小后返回的结果，再由返回的结果查询到的具体数据。
            pagedGridResult.setRows(vos);
            //插入当前页数
            pagedGridResult.setPage(page);
            //插入查询的总记录数
            pagedGridResult.setRecords(pageInfo.getTotal());
            //插入总页数（总记录数 / pageSize[每一页的可展示的数量]）
            pagedGridResult.setTotal(pageInfo.getPages());
        }
        return pagedGridResult;
    }

}
