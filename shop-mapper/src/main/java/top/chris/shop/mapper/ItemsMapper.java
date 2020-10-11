package top.chris.shop.mapper;

import top.chris.shop.pojo.Items;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.vo.CatItemListVo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsMapper extends tk.mybatis.mapper.common.Mapper<Items> {
    List<CatItemListVo> queryCatItems(SearchItemsBo catItemsBo);
    List<CatItemListVo> querySearchItemsLikeName(SearchItemsBo searchItemsBo);
    List<RenderItemInfoVo> renderItemInfo(String itemId);
    RenderItemInfoVo.SimpleItem querySimpleItemByItemId (String itemId);
}




