package top.chris.shop.service.impl;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chris.shop.mapper.ItemsImgMapper;
import top.chris.shop.mapper.ItemsMapper;
import top.chris.shop.mapper.ItemsParamMapper;
import top.chris.shop.mapper.ItemsSpecMapper;
import top.chris.shop.pojo.bo.CatItemsBo;
import top.chris.shop.pojo.bo.SearchItemsBo;
import top.chris.shop.pojo.vo.CatItemListVo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.service.ItemsService;
import top.chris.shop.util.PagedGridResult;

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

    //分页插件
    @Autowired
    private PageHelper pageHelper;

    /**
     * itemsBo对象中的sort值可能有三种情况：1、c表示销量优先(默认销量约高，越前)；2、k表示默认查询；3、p表示价格优先(默认价格约低，越前)
     * @param itemsBo
     * @return
     */
    @Override
    public PagedGridResult catItems(SearchItemsBo itemsBo) {
        itemsBo = checkParms(itemsBo);
        if (itemsBo.getCatId() == null ){
            throw new RuntimeException("请求中没有包含要查询商品的id");
        }
        String sort = itemsBo.getSort();
        if (StringUtils.equals(sort,"c") || StringUtils.equals(sort,"p") || StringUtils.equals(sort,"k")){
//            itemsBo.setSort("no");
            int allResultCount = itemsMapper.queryCatItems(itemsBo).size();
            //使用PageHelper设置分页，为了安全分页，后边最好紧跟mybatis mapper方法
            //注意这里看起来似乎是属于内存分页，但其实PageHelper插件对mybatis执行流程进行了增强，属于物理分页
            itemsBo.setSort(sort);
            pageHelper.startPage(itemsBo.getPage(),itemsBo.getPageSize());
            //获取分页查询后的结果。
            List<CatItemListVo> result = itemsMapper.queryCatItems(itemsBo);
            PagedGridResult pagedGridResult = new PagedGridResult();
            pagedGridResult.setRows(result);
            pagedGridResult.setPage(itemsBo.getPage());
            pagedGridResult.setRecords(allResultCount); //总记录数
            pagedGridResult.setTotal((int)Math.ceil(allResultCount/(itemsBo.getPageSize()*1.0)));
            System.out.println("一共被分为几个页面："+(int)Math.ceil(allResultCount/(itemsBo.getPageSize()*1.0))+";总记录数 = "+result.size());
            //返回分页模型
            return pagedGridResult;
        }else {
            throw new RuntimeException("查询依据不正确");
        }
    }

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
            //PagedGridResult pagedGridResult = new PagedGridResult(result,itemsBo.getPage());
            PagedGridResult pagedGridResult = new PagedGridResult();
            pagedGridResult.setRows(result);
            pagedGridResult.setPage(itemsBo.getPage());
            return pagedGridResult;
        }else {
            throw new RuntimeException("查询数据格式不正确");
        }
    }

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
     * 检查前端传入数据的合法性
     * @param itemsBo
     * @return
     */
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
