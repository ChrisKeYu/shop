package top.chris.shop.mapper;

import top.chris.shop.pojo.UserAddress;
import top.chris.shop.pojo.vo.adminVo.AdminUserAddressInfoVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface UserAddressMapper extends tk.mybatis.mapper.common.Mapper<UserAddress> {

    //多条件查询用户所有的地址信息(按照名称模糊查询)
    List<AdminUserAddressInfoVo> queryAllUserAddressInfo(String condition);

}




