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
    List<RenderItemInfoVo.SimpleItemsSpec> querySimpleItemsSpecByItemId(String itemId);
}




