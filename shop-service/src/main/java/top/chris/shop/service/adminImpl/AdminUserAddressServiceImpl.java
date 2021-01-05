package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.chris.shop.mapper.UserAddressMapper;
import top.chris.shop.pojo.UserAddress;
import top.chris.shop.pojo.bo.adminBo.AdminUserAddressBo;
import top.chris.shop.pojo.vo.adminVo.AdminUserAddressInfoVo;
import top.chris.shop.service.admin.AdminUserAddressService;
import top.chris.shop.util.PagedGridResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminUserAddressServiceImpl implements AdminUserAddressService {
    @Autowired
    private UserAddressMapper addressMapper;
    @Autowired
    private PageHelper pageHelper;
    @Autowired
    private Sid sid;

    @Override
    public PagedGridResult queryAllUserAddressInfo(String condition, Integer page, Integer pageSize) {
        PagedGridResult pagedGridResult = null;
        if (condition == ""){
            condition = null;
            pageHelper.startPage(page,pageSize);
            List<AdminUserAddressInfoVo> vos = addressMapper.queryAllUserAddressInfo(condition);
            if (vos.size() == 0){
                //该用户暂时还有没填入收货地址
                return null;
            }
            pagedGridResult = finfishPagedGridResult(vos, page);
        }else {
            //按名字模糊查询
            pageHelper.startPage(page,pageSize);
            List<AdminUserAddressInfoVo> vos = addressMapper.queryAllUserAddressInfo(condition);
            if (vos.size() == 0){
                //该用户暂时还有没填入收货地址
                return null;
            }
            pagedGridResult = finfishPagedGridResult(vos, page);
        }
        return pagedGridResult;
    }

    @Override
    public AdminUserAddressInfoVo queryUserAddressInfo(String addressId) {
        UserAddress address = addressMapper.selectByPrimaryKey(addressId);
        AdminUserAddressInfoVo vo = new AdminUserAddressInfoVo();
        vo.setId(address.getId());
        vo.setUserId(address.getUserId());
        vo.setMobile(address.getMobile());
        vo.setProvince(address.getProvince());
        vo.setCity(address.getCity());
        vo.setDistrict(address.getDistrict());
        vo.setDetail(address.getDetail());
        vo.setReceiver(address.getReceiver());
        vo.setExtand(address.getExtand());
        vo.setIsDefault(address.getIsDefault());
        vo.setCreatedTime(address.getCreatedTime());
        vo.setUpdatedTime(address.getUpdatedTime());
        return vo;
    }

    @Override
    public void updateUserAddressInfoById(AdminUserAddressBo bo) {
        UserAddress userAddress = addressMapper.selectByPrimaryKey(bo.getId());
        userAddress.setReceiver(bo.getMobile());
        userAddress.setMobile(bo.getReceiver());
        userAddress.setDetail(bo.getDetail());
        userAddress.setProvince(bo.getProvince());
        userAddress.setCity(bo.getCity());
        userAddress.setDistrict(bo.getDistrict());
        userAddress.setUpdatedTime(new Date());
        addressMapper.updateByPrimaryKey(userAddress);
    }

    @Override
    public void addUserAddressInfoById(AdminUserAddressBo bo) {
        UserAddress address = new UserAddress();
        address.setId(sid.nextShort());
        address.setUserId(bo.getUserId());
        address.setUpdatedTime(new Date());
        address.setDistrict(bo.getDistrict());
        address.setCity(bo.getCity());
        address.setProvince(bo.getProvince());
        address.setDetail(bo.getDetail());
        address.setMobile(bo.getMobile());
        address.setReceiver(bo.getReceiver());
        address.setIsDefault(0);
        address.setCreatedTime(new Date());
        address.setExtand(null);
        addressMapper.insert(address);
    }

    @Override
    public void delAddressByItemId(String id) {
        addressMapper.deleteByPrimaryKey(id);
    }

    /**
     * 用来封装查询好的地址信息
     * @param addresses
     * @return
     */
    public List<AdminUserAddressInfoVo> finfishAllAddressInfoVo(List<UserAddress> addresses){
        List<AdminUserAddressInfoVo> vos = new ArrayList<>();
        for (UserAddress address : addresses) {
            AdminUserAddressInfoVo vo = new AdminUserAddressInfoVo();
            vo.setId(address.getId());
            vo.setUserId(address.getUserId());
            vo.setMobile(address.getMobile());
            vo.setProvince(address.getProvince());
            vo.setCity(address.getCity());
            vo.setDistrict(address.getDistrict());
            vo.setDetail(address.getDetail());
            vo.setReceiver(address.getReceiver());
            vo.setExtand(address.getExtand());
            vo.setIsDefault(address.getIsDefault());
            vo.setCreatedTime(address.getCreatedTime());
            vo.setUpdatedTime(address.getUpdatedTime());
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
    public PagedGridResult finfishPagedGridResult(List<AdminUserAddressInfoVo> vos, Integer page){
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
