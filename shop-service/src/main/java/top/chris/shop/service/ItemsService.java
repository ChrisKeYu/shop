package top.chris.shop.service;

import top.chris.shop.pojo.bo.CatItemsBo;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

public interface ItemsService {
    /**
     *
     * @param itemsBo
     * @return PagedGridResult 返回的对象是一个分页的数据模型。
     */
    PagedGridResult catItems(SearchItemsBo itemsBo);
    PagedGridResult searchItemsLikeName(SearchItemsBo searchItemsBo);
    SearchItemsBo checkParms(SearchItemsBo itemsBo);
    RenderItemInfoVo queryItemPageInfo(String itemId);
}
