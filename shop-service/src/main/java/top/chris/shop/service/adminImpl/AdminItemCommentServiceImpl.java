package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chris.shop.mapper.ItemsCommentsMapper;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemCommentInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;
import top.chris.shop.service.admin.AdminItemCommentService;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

@Log
@Service
public class AdminItemCommentServiceImpl implements AdminItemCommentService {
    @Autowired
    private PageHelper pageHelper;
    @Autowired
    private ItemsCommentsMapper commentsMapper;

    @Override
    public PagedGridResult queryAllItemComment(String condition,Integer page, Integer pageSize) {
        if (condition.equals("")){
            condition = null;
        }
        pageHelper.startPage(page,pageSize);
        List<AdminItemCommentInfoVo> vos = commentsMapper.getAllAdminItemCommentInfo(condition);
        return finfishPagedGridResult(vos,page);
    }

    @Override
    public PagedGridResult queryItemCommentInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (bo.getItemName().equals("") && bo.getUserName().equals("")){
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
                    List<AdminItemCommentInfoVo> vos = commentsMapper.queryItemCommentByCatIdAndRootCatId(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }else {
                    //按照一级大类和三级大类去找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照一级大类和三级大类去找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos = commentsMapper.queryItemCommentByCatIdAndRootCatId(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }
            }
        }else if(bo.getUserName().equals("")){
            if (bo.getCategory0() == 0){
                bo.setCategory0(null);
                bo.setCatIdCatgory0(null);
                bo.setUserName(null);
                //按照商品名称进行模糊查询
                pageHelper.startPage(page,pageSize);
                log.info("按照商品名称进行模糊查询:"+ReflectionToStringBuilder.toString(bo));
                List<AdminItemCommentInfoVo> vos = commentsMapper.queryItemCommentByCondition(bo);
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                return pagedGridResult;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    bo.setCatIdCatgory0(null);
                    bo.setUserName(null);
                    //按照商品名称和一级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称和一级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos1 = commentsMapper.queryItemCommentByCondition(bo);
                    if (vos1.size() == 0) {
                        //查询结果为0，在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos1, page);
                    return pagedGridResult;
                }else {
                    bo.setUserName(null);
                    //按照商品名称、一级分类和三级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称、一级分类和三级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos1 = commentsMapper.queryItemCommentByCondition(bo);
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
        }else { //按照商品名称、用户名称、一级、二级分类查询
            if (bo.getCategory0() == 0){//按照商品名称或用户名称查询
                bo.setCategory0(null);
                bo.setCatIdCatgory0(null);
                if (!bo.getItemName().equals("") && bo.getUserName().equals("")){
                    bo.setUserName(null);
                    //按照商品名称进行模糊查询
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称进行模糊查询:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos = commentsMapper.queryItemCommentByCondition(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }else if (bo.getItemName().equals("") && !bo.getUserName().equals("")){
                    bo.setItemName(null);
                    //按照用户名称进行查询
                    pageHelper.startPage(page,pageSize);
                    log.info("按照用户名称进行模糊查询:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos = commentsMapper.queryItemCommentByCondition(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }else {
                    //按照商品名称和用户名称进行模糊查询
                    pageHelper.startPage(page,pageSize);
                    log.info("按照用户名称进行模糊查询:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos = commentsMapper.queryItemCommentByUserNameAndItemNameAndCat(bo);
                    log.info("按照用户名称进行模糊查询的结果："+ReflectionToStringBuilder.toString(vos));
                    if (vos.size() == 0) {
                        //查询结果为0，在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    //按照用户名称、商品名称、一级查询
                    bo.setCatIdCatgory0(null);
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称、用户名称、一级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos1 = commentsMapper.queryItemCommentByUserNameAndItemNameAndCat(bo);
                    if (vos1.size() == 0) {
                        //查询结果为0，根据上市条件在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos1, page);
                    return pagedGridResult;
                }else {
                    //按照用户名称、商品名称、一级和二级分类查询
                    //按照商品名称、一级分类和三级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称、用户名称、一级分类和三级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemCommentInfoVo> vos1 = commentsMapper.queryItemCommentByUserNameAndItemNameAndCat(bo);
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

    @Override
    public String deleteItemCommentInfoBySpecId(String commentID) {
        String result = null;
        if (commentsMapper.selectByPrimaryKey(commentID) == null){
            return result;
        }
        commentsMapper.deleteByPrimaryKey(commentID);
        return "success";
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminItemCommentInfoVo> vos, Integer page){
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
