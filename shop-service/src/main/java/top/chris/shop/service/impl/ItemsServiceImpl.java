package top.chris.shop.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.enums.CommentLevelEnum;
import top.chris.shop.exception.ItemException;
import top.chris.shop.exception.StockException;
import top.chris.shop.mapper.*;
import top.chris.shop.pojo.Items;
import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.ItemsSpec;
import top.chris.shop.pojo.bo.CommentBo;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.dto.ItemCommentLevelDto;
import top.chris.shop.pojo.vo.*;
import top.chris.shop.service.ItemsService;
import top.chris.shop.util.PagedGridResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsServiceImpl implements ItemsService {
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsSpecMapper specMapper;
    @Autowired
    private ItemsParamMapper paramMapper;
    @Autowired
    private ItemsImgMapper imgMapper;
    @Autowired
    private ItemsCommentsMapper commentsMapper;
    @Autowired
    private PageHelper pageHelper;    //分页插件

    /**
     * itemsBo对象中的sort值可能有三种情况：1、c表示销量优先(默认销量约高，越前)；2、k表示默认查询；3、p表示价格优先(默认价格约低，越前)
     * @param itemsBo 前端传入的参数
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult catItems(SearchItemsBo itemsBo) {
        itemsBo = checkParms(itemsBo);
        if (itemsBo.getCatId() == null ){
            throw new RuntimeException("请求中没有包含要查询商品的id");
        }
        String sort = itemsBo.getSort();
        if (StringUtils.equals(sort,"c") || StringUtils.equals(sort,"p") || StringUtils.equals(sort,"k")){
//           itemsBo.setSort("no");
            //使用PageHelper设置分页，为了安全分页，后边最好紧跟mybatis mapper方法
            //注意这里看起来似乎是属于内存分页，但其实PageHelper插件对mybatis执行流程进行了增强，属于物理分页
            itemsBo.setSort(sort);
            //pageHelper.startPage(itemsBo.getPage(),itemsBo.getPageSize());
            //获取分页查询后的结果。
            List<CatItemListVo> result = itemsMapper.queryCatItems(itemsBo);
            PagedGridResult pagedGridResult = new PagedGridResult();
            PageInfo<?> pageInfo = new PageInfo<>(result);
            pagedGridResult.setRows(result);
            pagedGridResult.setPage(itemsBo.getPage());
            pagedGridResult.setRecords(pageInfo.getTotal()); //总记录数
            pagedGridResult.setTotal(pageInfo.getPages());
            return pagedGridResult;
        }else {
            throw new RuntimeException("查询依据不正确");
        }
    }

    /**
     * 搜索框的模糊查询，参数接受已封装好的前端数据，返回以分页的形式返回查询的数据。
     * @param itemsBo
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsLikeName(SearchItemsBo itemsBo) {
        itemsBo = checkParms(itemsBo);
        if (StringUtils.isBlank(itemsBo.getKeywords())){
            throw new RuntimeException("请求中没有包含要查询商品的名称");
        }
        String sort = itemsBo.getSort();
        if (StringUtils.equals(sort,"c") || StringUtils.equals(sort,"p") || StringUtils.equals(sort,"k")){
            //使用PageHelper设置分页，为了安全分页，后边最好紧跟mybatis mapper方法
            //注意这里看起来似乎是属于内存分页，但其实PageHelper插件对mybatis执行流程进行了增强，属于物理分页
            pageHelper.startPage(itemsBo.getPage(),itemsBo.getPageSize());
            List<CatItemListVo> result = itemsMapper.querySearchItemsLikeName(itemsBo);
            PagedGridResult pagedGridResult = new PagedGridResult();
            PageInfo<?> pageInfo = new PageInfo<>(result);
            pagedGridResult.setRows(result);
            pagedGridResult.setPage(itemsBo.getPage());
            pagedGridResult.setRecords(pageInfo.getTotal());
            pagedGridResult.setTotal(pageInfo.getPages());
            return pagedGridResult;
        }else {
            throw new RuntimeException("查询数据格式不正确");
        }
    }

    /**
     * 商品详情查询，接受商品的id，返回商品详情的数据。
     * @param itemId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public RenderItemInfoVo queryItemPageInfo(String itemId) {
        if (StringUtils.isBlank(itemId) || StringUtils.isEmpty(itemId)){
            throw new RuntimeException("商品的id查询依据不正确");
        }
        RenderItemInfoVo renderItemInfoVo = new RenderItemInfoVo();
        //多次查询，每次查询不同的表格，最后把查询的数据赋值到数据模型上
        renderItemInfoVo.setItem(itemsMapper.querySimpleItemByItemId(itemId));
        renderItemInfoVo.setItemImgList(imgMapper.querySimpleItemsImgByItemId(itemId));
        renderItemInfoVo.setItemParams(paramMapper.querySimpleItemsParamByItemId(itemId));
        renderItemInfoVo.setItemSpecList(specMapper.querySimpleItemsSpecByItemId(itemId));

        return renderItemInfoVo;
    }

    /**
     * 商品评论各等级数量的查询，返回统计好的各评论的数量模型。
     * @param itemId 商品id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CountsVo renderCommentLevelyItemId(String itemId) {
        CountsVo countsVo = new CountsVo();
        List<ItemCommentLevelDto> commentLevelDtos = commentsMapper.getCommentForEveryLevelByItemId(itemId);
        //迭代各评论的查询结果
        for (ItemCommentLevelDto dto: commentLevelDtos) {
            //判断是否为“好评”（表中数据“1”代表枚举类型中的“好评”）
            if (dto.getCommentLevel()== CommentLevelEnum.GOOD.type){
                countsVo.setGoodCounts(dto.getCounts());
                countsVo.setTotalCounts(countsVo.getTotalCounts()+dto.getCounts());
            }
            //判断是否为“中评”
            if (dto.getCommentLevel()== CommentLevelEnum.NORMAL.type){
                countsVo.setNormalCounts(dto.getCounts());
                countsVo.setTotalCounts(countsVo.getTotalCounts()+dto.getCounts());
            }
            //判断是否为“差评”
            if (dto.getCommentLevel()== CommentLevelEnum.BAD.type){
                countsVo.setBadCounts(dto.getCounts());
                countsVo.setTotalCounts(countsVo.getTotalCounts()+dto.getCounts());
            }
        }
        return countsVo;
    }

    /**
     * 商品评论各的查询
     * @param commentBo 前端传入的参数
     * @param page
     * @param pageSize
     * @return 返回CommentRecordVo集合的数据模型。
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult renderCommentByItemIdAndLevel(CommentBo commentBo,Integer page, Integer pageSize) {
        //使用PageHelper进行分页查询
        PageHelper.startPage(page,pageSize);
        List<CommentRecordVo> result = commentsMapper.getCommentByItemIdAndLevel(commentBo);
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (result != null){
            PageInfo<?> pageInfo = new PageInfo<>(result);
            //插入数据
            pagedGridResult.setRows(result);
            //插入当前页数
            pagedGridResult.setPage(page);
            //插入查询的总记录数
            pagedGridResult.setRecords(pageInfo.getTotal());
            //插入总页数（总记录数 / pageSize[每一页的可展示的数量]）
            pagedGridResult.setTotal(pageInfo.getPages());
        }
        return pagedGridResult;
    }

    /**
     * 根据指定的多个ids查询商品信息
     * @param itemSpecIds
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecByitemSpecIds(List<String> itemSpecIds) {
        Example example = new Example(ItemsSpec.class);
        example.createCriteria().andIn("id",itemSpecIds);
        return specMapper.selectByExample(example);
    }

    /**
     * 根据指定id查询商品的图片(有了购物车数据库，就不需要这个方法了)
     * @param itemId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsImg queryItemImgByItemId(String itemId) {
        Example example = new Example(ItemsImg.class);
        example.createCriteria().andEqualTo("itemId",itemId).andEqualTo("isMain","1");
        return imgMapper.selectByExample(example).get(0);
    }

    /**
     * 根据指定id查询商品的信息(有了购物车数据库，就不需要这个方法了)
     * @param itemId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemByItemId(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    /**
     * 减少商品的库存
     * @param itemsSpec 最新的商品规格对象，已经修改过库存的
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer decreaseItemSpecStock(ItemsSpec itemsSpec) {
        //在mapper中设置了以锁，只有当传入的对象中的stock参数小于数据库中对应商品库存量的时候，才会扣除库存，否则发生超卖行为
        int result = specMapper.updateItemSpecStock(itemsSpec);
        if (result != 1){
            throw new StockException("库存扣除失败，有可能是库存数量不足");
        }
        return result;
    }

    /**
     * 根据商品Id和商品口味查询对应商品Id的库存量
     * @param specId 商品规格id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer queryItemStockByItemId(String specId) {
        ItemsSpec itemsSpec = specMapper.selectByPrimaryKey(specId);
        if (itemsSpec == null){
            throw new ItemException("数据库中没有该商品的规格参数");
        }
        return itemsSpec.getStock();
    }

    /**
     * 商品详情查询，接受商品的id，返回商品详情的数据。
     * @param itemId 商品id
     * @param specId 规格id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public RenderItemInfoVo queryCartInfoByitemIdAndSpecId(String itemId,String specId) {
        if (StringUtils.isBlank(itemId) || StringUtils.isEmpty(itemId) && StringUtils.isBlank(specId) || StringUtils.isEmpty(specId)){
            throw new RuntimeException("商品的id或商品的类型id的查询依据不正确");
        }
        RenderItemInfoVo renderItemInfoVo = new RenderItemInfoVo();
        //多次查询，每次查询不同的表格，最后把查询的数据赋值到数据模型上
        renderItemInfoVo.setItem(itemsMapper.querySimpleItemByItemId(itemId));
        renderItemInfoVo.setItemImgList(imgMapper.querySimpleItemsImgByItemId(itemId));
        renderItemInfoVo.setItemParams(paramMapper.querySimpleItemsParamByItemId(itemId));
        renderItemInfoVo.setItemSpecList(specMapper.querySimpleItemsSpecBySpecId(specId));
        return renderItemInfoVo;
    }

    /**
     * 根据商品属性id获取ItemSpec对象中商品的id
     * @param specId 规格id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemIdByItemSpecId(String specId) {
        return specMapper.selectByPrimaryKey(specId).getItemId();
    }

    /**
     * 查询商品的具体内容
     * @param itemId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ContentVo queryItemDetailInfo(String itemId) {
        Items items = itemsMapper.selectByPrimaryKey(itemId);
        ContentVo contentVo = new ContentVo();
        contentVo.setContent(items.getContent());
        List<String> images = new ArrayList<>();
        Example example = new Example(ItemsImg.class);
        example.createCriteria().andEqualTo("itemId",itemId);
        for (ItemsImg itemsImg : imgMapper.selectByExample(example)) {
            images.add(itemsImg.getUrl());
        }
        contentVo.setImages(images);
        return contentVo;
    }

    /**
     * 检查前端传入数据的合法性
     * @param itemsBo
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public SearchItemsBo checkParms(SearchItemsBo itemsBo) {
        if (itemsBo.getPage() == null){
            //如果前端没有传入分页开始页面，默认开始页面为1
            itemsBo.setPage(1);
        }

        if (itemsBo.getPageSize() == null){
            //如果前端没有传入每页展示数量，默认每一页展现10条数据
            itemsBo.setPageSize(10);
        }

        if (!StringUtils.isNotBlank(itemsBo.getSort())){
            //如果前端没有传入商品按什么排序即（sort=null），默认商品按更新时间排序
            itemsBo.setSort("k");
        }
        return itemsBo;
    }

}
