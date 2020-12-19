package top.chris.shop.service;

import org.springframework.web.bind.annotation.RequestParam;
import top.chris.shop.pojo.Items;
import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.ItemsSpec;
import top.chris.shop.pojo.bo.CatItemsBo;
import top.chris.shop.pojo.bo.CommentBo;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.dto.ItemCommentLevelDto;
import top.chris.shop.pojo.vo.CommentRecordVo;
import top.chris.shop.pojo.vo.CountsVo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.pojo.vo.ShopCartVo;
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
    CountsVo renderCommentLevelyItemId(String itemId);

    //商品评论各的查询，返回CommentRecordVo集合的数据模型。
    PagedGridResult renderCommentByItemIdAndLevel(CommentBo commentBo,Integer page, Integer pageSize);

    //根据指定的多个ids查询商品信息
    List<ItemsSpec> queryItemSpecByitemSpecIds(List<String> itemSpecIds);

    //根据指定id查询商品的图片(有了购物车数据库，就不需要这个方法了)
    ItemsImg queryItemImgByItemId(String itemId);

    //根据指定id查询商品的信息(有了购物车数据库，就不需要这个方法了)
    Items queryItemByItemId(String itemId);

    //减少商品的库存
    Integer decreaseItemSpecStock(ItemsSpec itemsSpec);
    
    //根据商品Id和商品口味查询对应商品Id的库存量
    Integer queryItemStockByItemId(String specId);

    //商品详情查询，接受商品的id，返回商品详情的数据。
    RenderItemInfoVo queryCartInfoByitemIdAndSpecId(String itemId,String specId);

    //根据商品属性id获取ItemSpec对象中商品的id
    String queryItemIdByItemSpecId(String specId);

}

//购物车商品展示,接受前端传过来的购物车内的所有商品id   (放在了购物车控制层实现了，不需要再这里实现)
//    List<ShopCartVo> renderShopCart(String[] itemSpecIds);
