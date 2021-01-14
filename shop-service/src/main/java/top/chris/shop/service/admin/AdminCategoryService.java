package top.chris.shop.service.admin;

import org.springframework.web.bind.annotation.RequestParam;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminCategoryInfoVo;
import top.chris.shop.util.PagedGridResult;

public interface AdminCategoryService {
    //按条件查询分级信息
    PagedGridResult queryAllCategoryInfo(String condition , Integer page, Integer pageSize);
    //多条件查询所有的轮播图信息
    PagedGridResult queryCategoryInfoByCondition(AdminSearchItemParamBo bo,Integer page, Integer pageSize);

    //根据id获取分类信息
    AdminCategoryInfoVo queryCategoryInfoById(String id);
}
