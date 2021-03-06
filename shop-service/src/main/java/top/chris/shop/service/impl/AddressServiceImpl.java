package top.chris.shop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.n3r.idworker.Sid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.chris.shop.exception.AddressException;
import top.chris.shop.mapper.UserAddressMapper;
import top.chris.shop.pojo.UserAddress;
import top.chris.shop.pojo.bo.AddressBo;
import top.chris.shop.pojo.vo.AddressVo;
import top.chris.shop.service.AddressService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private Sid sid;

    @Autowired
    private UserAddressMapper addressMapper;

    /**
     * 添加用户地址信息
     * @param addressBo 前端传入的用户地址信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addUserAddress(AddressBo addressBo) {
        UserAddress userAddress = new UserAddress();
        addressBo.setUpdatedTime(new Date());
        //新添加的地址都为非默认地址
        addressBo.setIsDefault(0);
        userAddress.setId(sid.nextShort());
        userAddress.setCreatedTime(new Date());
        userAddress.setExtand(null);
        BeanUtils.copyProperties(addressBo,userAddress);
        log.info(ReflectionToStringBuilder.toString(userAddress, ToStringStyle.MULTI_LINE_STYLE));
        addressMapper.insert(userAddress);
    }

    /**
     * 查询指定用户所有地址信息
     * @param userId 用户id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AddressVo> renderUserAddress(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        List<AddressVo> renderList = new ArrayList<AddressVo>();
        List<UserAddress> lists= addressMapper.select(userAddress);
        for (UserAddress address :lists
        ) {
            AddressVo vo = new AddressVo(address.getId(),address.getReceiver(),address.getMobile(),address.getProvince(),
                    address.getDistrict(),address.getDetail(),address.getCity(),address.getIsDefault());
            renderList.add(vo);
        }
        return renderList;
    }

    /**
     * 修改用户指定的地址信息
     * @param addressBo 前端传入的信息
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer updateUserAddress(AddressBo addressBo) {
        //检测数据库中是否存指定的地址数据
        UserAddress userAddress = addressMapper.selectByPrimaryKey(addressBo.getAddressId());
        if (userAddress == null){
            throw new AddressException("请求中的地址在数据库中不存在");
        }
        //BeanUtils是一个工具类，copyProperties方法会将源对象与目标对象中相同名称的属性值进行一个对比，只要源对象的属性值与目标对象的值不同，就会覆盖掉目标对象的值
        BeanUtils.copyProperties(addressBo,userAddress);
        userAddress.setIsDefault(0);
        userAddress.setUpdatedTime(new Date());
        log.info(ReflectionToStringBuilder.toString(userAddress, ToStringStyle.MULTI_LINE_STYLE));
        return addressMapper.updateByPrimaryKey(userAddress);
    }

    /**
     * 修改指定用户指定的地址为默认地址
     * @param userId 用户id
     * @param addressId 地址id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer updateDefalutUserAddress(String userId, String addressId) {
        List<UserAddress> lsit = addressMapper.selectAll();
        for (UserAddress userAddress:lsit
             ) {
            if (userAddress.getIsDefault() == 1){
                //默认地址只有一个，因此当用户再次修改默认地址时，需要把原来设置的默认地址设为0
                userAddress.setIsDefault(0);
                addressMapper.updateByPrimaryKey(userAddress);
            }
        }
        //检测数据库中是否存指定的地址数据
        UserAddress userAddress = addressMapper.selectByPrimaryKey(addressId);
        if (userAddress == null){
            throw new AddressException("请求中的地址在数据库中不存在");
        }
        userAddress.setUpdatedTime(new Date());
        userAddress.setIsDefault(1);
        return addressMapper.updateByPrimaryKey(userAddress);
    }

    /**
     * 删除指定用户的指定地址信息
     * @param userId 用户id
     * @param addressId 地址id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer deleteUserAddress(String userId, String addressId) {
        //检测数据库中是否存指定的地址数据
        UserAddress userAddress = addressMapper.selectByPrimaryKey(addressId);
        if (userAddress == null){
            throw new AddressException("请求中的地址在数据库中不存在");
        }
        return addressMapper.deleteByPrimaryKey(addressId);
    }

    /**
     * 查询用户指定的地址信息
     * @param addressId 地址id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddressByAddressId(String addressId) {
        return addressMapper.selectByPrimaryKey(addressId);
    }
}
