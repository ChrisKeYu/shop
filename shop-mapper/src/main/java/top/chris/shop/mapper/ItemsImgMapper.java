package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.vo.RenderItemInfoVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsImgMapper extends tk.mybatis.mapper.common.Mapper<ItemsImg> {
    List<RenderItemInfoVo.SimpleItemsImg> querySimpleItemsImgByItemId(String itemId);
}




