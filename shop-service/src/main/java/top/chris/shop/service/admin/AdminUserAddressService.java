package top.chris.shop.service.admin;

import org.springframework.web.bind.annotation.RequestParam;
import top.chris.shop.pojo.bo.adminBo.AdminUserAddressBo;
import top.chris.shop.pojo.vo.adminVo.AdminUserAddressInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

public interface AdminUserAddressService {
    //查询所有用户的收货地址
    PagedGridResult queryAllUserAddressInfo(String condition, Integer page, Integer pageSize);

    //获取根据addressId获取指定收货地址信息
    AdminUserAddressInfoVo queryUserAddressInfo(String addressId);

    //修改指定用户的address地址信息
    void updateUserAddressInfoById(AdminUserAddressBo bo);

    //为指定用户添加新的address地址信息
    void addUserAddressInfoById(AdminUserAddressBo bo);

    //根据Id指定删除地址
    void delAddressByItemId(String id);
}
