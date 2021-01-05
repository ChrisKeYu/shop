package top.chris.shop.mapper;

import top.chris.shop.pojo.Users;
import top.chris.shop.pojo.vo.adminVo.UserOrderStatusVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface UsersMapper extends tk.mybatis.mapper.common.Mapper<Users> {
    //根据用户id查询该用户下的所有订单状态
    List<UserOrderStatusVo> queryUserOrderStatusByUserId(String userId);
}




