package top.chris.shop.service.admin;

import top.chris.shop.pojo.bo.adminBo.AdminItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemSpecInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

public interface AdminItemParamService {

    //查询所有商品的参数信息
    PagedGridResult queryAllItemParam(Integer page, Integer pageSize);

    //根据ItemId查询指定商品的参数信息
    AdminItemParamVo queryItemParamByItemId(String paramId);

    //根据商品参数id修改指定的商品参数
    void updateItemParamById(AdminItemParamBo bo);

    //根据ParamId删除指定商品参数
    String delItemParamById(String id);

    //根据多条件查询ItemParamInfo
    PagedGridResult queryItemParamInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize);


}
