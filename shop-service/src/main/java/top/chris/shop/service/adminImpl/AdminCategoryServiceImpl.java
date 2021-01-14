package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chris.shop.mapper.CategoryMapper;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminCategoryInfoVo;
import top.chris.shop.service.admin.AdminCategoryService;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

@Log
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Autowired
    private CategoryMapper mapper;
    @Autowired
    private PageHelper pageHelper;

    @Override
    public PagedGridResult queryAllCategoryInfo(String condition, Integer page, Integer pageSize) {
        PagedGridResult pagedGridResult = null;
        if (condition.equals("")){
            condition = null;
            log.info("查询全部分类");
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        } else if (condition.equals("1")){
            log.info("查询一级分类:"+condition);
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else if (condition.equals("2")){
            log.info("查询二级分类:"+condition);
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else {
            log.info("查询三级分类:"+condition);
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }
    }

    @Override
    public PagedGridResult queryCategoryInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (!bo.getItemName().equals("")){
            //按照商品名称查找
            pageHelper.startPage(page,pageSize);
            log.info("按照商品名称查找:"+ReflectionToStringBuilder.toString(bo));
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByName(bo.getItemName());
            if (vos.size() == 0) {
                //查询结果为0，根据上市条件在数据库中查询不到数据
                PagedGridResult result0 = new PagedGridResult();
                result0.setRows(null);
                return result0;
            }
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else {
            return null;
        }
    }

    @Override
    public AdminCategoryInfoVo queryCategoryInfoById(String id) {
        return  mapper.rendersCategoryById(id);
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminCategoryInfoVo> vos, Integer page){
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
