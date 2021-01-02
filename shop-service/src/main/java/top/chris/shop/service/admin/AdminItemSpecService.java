package top.chris.shop.service.admin;

import top.chris.shop.pojo.bo.adminBo.AdminItemSpecBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemSpecInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.Map;

public interface AdminItemSpecService {
    //查询所有规格的商品信息
    PagedGridResult queryAllItemSpec(Integer page,Integer pageSize);

    //根据多条件查询ItemParamInfo
    PagedGridResult queryItemSpecInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize);

    //根据SpecId查询对应商品规格的信息
    AdminItemSpecInfoVo queryItemSpecInfoBySpecId(String specId);

    //根据SpecId修改对应商品的规格信息
    void updateItemSpecInfoBySpecId(AdminItemSpecBo bo);

    //根据SpecId删除对应的视频规格
    Map<String,String> deleteItemSpecInfoBySpecId(String specId,String itemId);
}
