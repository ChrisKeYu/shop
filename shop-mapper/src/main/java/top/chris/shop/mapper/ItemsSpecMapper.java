package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsSpec;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemSpecInfoVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsSpecMapper extends tk.mybatis.mapper.common.Mapper<ItemsSpec> {
    //根据ItemId查询商品属性信息
    List<RenderItemInfoVo.SimpleItemsSpec> querySimpleItemsSpecByItemId(String itemId);
    //根据ItemSpecId(主键)查询商品的属性信息
    List<RenderItemInfoVo.SimpleItemsSpec> querySimpleItemsSpecBySpecId(String specId);
    //修改库存
    Integer updateItemSpecStock(ItemsSpec itemsSpec);
    //查询所有商品规格的信息
    List<AdminItemSpecInfoVo> queryAllItemsSpec();
    //根据名称、一级和三级查询商品的参数信息
    List<AdminItemSpecInfoVo> queryItemSpecByCondition(AdminSearchItemParamBo bo);
    //根据一级和三级查询具体商品的参数信息
    List<AdminItemSpecInfoVo> queryItemSpecByCatIdAndRootCatId(AdminSearchItemParamBo bo);
}




