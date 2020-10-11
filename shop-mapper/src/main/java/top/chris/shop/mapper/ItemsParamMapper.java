package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsParam;
import top.chris.shop.pojo.vo.RenderItemInfoVo;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsParamMapper extends tk.mybatis.mapper.common.Mapper<ItemsParam> {
    RenderItemInfoVo.SimpleItemsParam querySimpleItemsParamByItemId(String itemId);
}




