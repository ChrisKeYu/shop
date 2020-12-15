package top.chris.shop.service;

import org.springframework.web.bind.annotation.RequestParam;
import top.chris.shop.pojo.UserAddress;
import top.chris.shop.pojo.bo.AddressBo;
import top.chris.shop.pojo.vo.AddressVo;

import java.util.List;

public interface AddressService {

    //添加用户地址信息
    void addUserAddress(AddressBo addressBo);

    //查询指定用户所有地址信息
    List<AddressVo> renderUserAddress(String userId);

    //修改用户指定的地址信息
    Integer updateUserAddress(AddressBo addressBo);

    //修改指定用户指定的地址为默认地址
    Integer updateDefalutUserAddress(String userId,String addressId);

    //删除指定用户的指定地址信息
    Integer deleteUserAddress(String userId,String addressId);

    //查询用户指定的地址信息
    UserAddress queryUserAddressByAddressId(String addressId);
}
