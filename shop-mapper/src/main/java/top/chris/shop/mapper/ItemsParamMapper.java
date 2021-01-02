package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsParam;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsParamMapper extends tk.mybatis.mapper.common.Mapper<ItemsParam> {
    RenderItemInfoVo.SimpleItemsParam querySimpleItemsParamByItemId(String itemId);

    List<AdminItemParamVo> queryAllItemParam();
    //根据名称、一级和三级查询商品的参数信息
    List<AdminItemParamVo> queryItemParamByCondition(AdminSearchItemParamBo bo);
    //根据一级和三级查询具体商品的参数信息
    List<AdminItemParamVo> queryItemParamByCatIdAndRootCatId(AdminSearchItemParamBo bo);

}




