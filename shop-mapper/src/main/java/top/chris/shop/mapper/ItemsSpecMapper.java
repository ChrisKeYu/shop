package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsSpec;
import top.chris.shop.pojo.vo.RenderItemInfoVo;

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
    Integer updateItemSpecStock(ItemsSpec itemsSpec);
}




