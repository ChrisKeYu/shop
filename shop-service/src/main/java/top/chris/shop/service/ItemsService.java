package top.chris.shop.service;

import top.chris.shop.pojo.bo.CatItemsBo;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.dto.ItemCommentLevelDto;
import top.chris.shop.pojo.vo.CountsVo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

public interface ItemsService {
    /**
     *商品类别的查询
     * @param itemsBo
     * @return PagedGridResult 返回的对象是一个分页的数据模型。
     */
    PagedGridResult catItems(SearchItemsBo itemsBo);
    //搜索框的模糊查询，参数接受已封装好的前端数据，返回以分页的形式返回查询的数据。
    PagedGridResult searchItemsLikeName(SearchItemsBo searchItemsBo);
    //检查前端传入数据的合法性
    SearchItemsBo checkParms(SearchItemsBo itemsBo);
    //商品详情查询，接受商品的id，返回商品详情的数据。
    RenderItemInfoVo queryItemPageInfo(String itemId);
    //商品评论各等级数量的查询，返回统计好的各评论的数量模型。
    CountsVo renderCommentLevel(String itemId);
}
