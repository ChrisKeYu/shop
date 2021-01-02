package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chris.shop.mapper.ItemsMapper;
import top.chris.shop.mapper.ItemsParamMapper;
import top.chris.shop.pojo.Items;
import top.chris.shop.pojo.ItemsParam;
import top.chris.shop.pojo.bo.adminBo.AdminItemParamBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemImagesInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;
import top.chris.shop.service.admin.AdminItemParamService;
import top.chris.shop.util.PagedGridResult;

import java.util.Date;
import java.util.List;

@Log
@Service
public class AdminItemParamServiceImpl implements AdminItemParamService {
    @Autowired
    private ItemsParamMapper paramMapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private PageHelper pageHelper;


    @Override
    public PagedGridResult queryAllItemParam(Integer page, Integer pageSize) {
        pageHelper.startPage(page,pageSize);
        List<AdminItemParamVo> adminItemParamVos = paramMapper.queryAllItemParam();
        return finfishPagedGridResult(adminItemParamVos,page) ;
    }

    @Override
    public AdminItemParamVo queryItemParamByItemId(String paramId) {
        ItemsParam itemsParam = paramMapper.selectByPrimaryKey(paramId);
        AdminItemParamVo vo = new AdminItemParamVo();
        vo.setId(itemsParam.getId());
        vo.setProducPlace(itemsParam.getProducPlace());
        vo.setFootPeriod(itemsParam.getFootPeriod());
        vo.setPackagingMethod(itemsParam.getPackagingMethod());
        vo.setFactoryAddress(itemsParam.getFactoryAddress());
        vo.setBrand(itemsParam.getBrand());
        vo.setFactoryName(itemsParam.getFactoryName());
        vo.setFactoryAddress(itemsParam.getFactoryAddress());
        vo.setPackagingMethod(itemsParam.getPackagingMethod());
        vo.setWeight(itemsParam.getWeight());
        vo.setStorageMethod(itemsParam.getStorageMethod());
        vo.setEatMethod(itemsParam.getEatMethod());
        return vo;
    }

    @Override
    public void updateItemParamById(AdminItemParamBo bo) {
        ItemsParam itemsParam = paramMapper.selectByPrimaryKey(bo.getId());
        itemsParam.setProducPlace(bo.getProducPlace());
        itemsParam.setFootPeriod(bo.getFootPeriod());
        itemsParam.setBrand(bo.getBrand());
        itemsParam.setFactoryName(bo.getFactoryName());
        itemsParam.setFactoryAddress( bo.getFactoryAddress());
        itemsParam.setPackagingMethod(bo.getPackagingMethod());
        itemsParam.setWeight(bo.getWeight());
        itemsParam.setStorageMethod(bo.getStorageMethod());
        itemsParam.setEatMethod(bo.getEatMethod());
        itemsParam.setUpdatedTime(new Date());
        paramMapper.updateByPrimaryKey(itemsParam);
    }

    @Override
    public String delItemParamById(String id) {
        ItemsParam itemsParam = paramMapper.selectByPrimaryKey(id);
        Items items = itemsMapper.selectByPrimaryKey(itemsParam.getItemId());
        if (items.getOnOffStatus() == 2){
            //只有下架了的商品才可以进行删除
            paramMapper.deleteByPrimaryKey(itemsParam);
            return "success";
        }
        return "只有下架了的商品才可以删除改商品的参数数据";
    }

    @Override
    public PagedGridResult queryItemParamInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (bo.getItemName().equals("")){
            bo.setItemName(null); //方便SQL语句实现动态查询
            //只需要根据一级、二级分类条件进行查询
            if (bo.getCategory0() == 0){
                //如果一级分类也没有选择，那么就直接返回一个null，并告知：请填入任何一个搜索条件
                return null;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    bo.setCatIdCatgory0(null);//方便SQL语句实现动态查询
                    //按一级大类去查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按一级大类去查找:"+ ReflectionToStringBuilder.toString(bo));
                    List<AdminItemParamVo> vos = paramMapper.queryItemParamByCatIdAndRootCatId(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }else {
                    //按照一级大类和三级大类去找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照一级大类和三级大类去找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemParamVo> vos = paramMapper.queryItemParamByCatIdAndRootCatId(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }
            }
        }else {
            if (bo.getCategory0() == 0){
                bo.setCategory0(null);
                bo.setCatIdCatgory0(null);
                //按照商品名称进行模糊查询
                pageHelper.startPage(page,pageSize);
                log.info("按照商品名称进行模糊查询:"+ReflectionToStringBuilder.toString(bo));
                List<AdminItemParamVo> vos = paramMapper.queryItemParamByCondition(bo);
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                return pagedGridResult;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    bo.setCatIdCatgory0(null);
                    //按照商品名称和一级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称和一级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemParamVo> vos1 = paramMapper.queryItemParamByCondition(bo);
                    if (vos1.size() == 0) {
                        //查询结果为0，在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos1, page);
                    return pagedGridResult;
                }else {
                    //按照商品名称、一级分类和三级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称、一级分类和三级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemParamVo> vos1 = paramMapper.queryItemParamByCondition(bo);
                    if (vos1.size() == 0) {
                        //查询结果为0，根据上市条件在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos1, page);
                    return pagedGridResult;
                }
            }
        }
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminItemParamVo> vos, Integer page){
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (vos != null || vos.size() != 0){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(vos);
            //插入查询到的指定数据，与上面传入的参数是不一致的，下面传入的参数是orders对象被pageHelper设定固定大小后返回的结果，再由返回的结果查询到的具体数据。
            pagedGridResult.setRows(vos);
            //插入当前页数
            pagedGridResult.setPage(page);
            //插入查询的总记录数
            pagedGridResult.setRecords(pageInfo.getTotal());
            //插入总页数（总记录数 / pageSize[每一页的可展示的数量]）
            pagedGridResult.setTotal(pageInfo.getPages());
        }
        return pagedGridResult;
    }
}
