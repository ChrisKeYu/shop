package top.chris.shop.service.admin;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.AdminCategoryBo;
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
    //添加一级分类信息
    Integer addFirstCategory(AdminCategoryBo bo,MultipartFile file);
    //添加二三级分类信息
    Integer addOtherCategory(AdminCategoryBo bo);
    //修改一二三级分类信息
    Integer updateCategoryInfo(AdminCategoryBo bo);
    //修改一级分类的图标片
    Integer updateFirstCategory(String id,MultipartFile file);
}
