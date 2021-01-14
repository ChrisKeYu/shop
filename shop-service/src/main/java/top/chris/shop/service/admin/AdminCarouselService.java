package top.chris.shop.service.admin;

import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.AdminCarouselBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminCarouselInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemImagesInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.List;

public interface AdminCarouselService {
    //查询所有轮播图信息
    PagedGridResult queryAllCarousel(String condition,Integer page, Integer  pageSize);

    //多条件查询所有的轮播图信息
    PagedGridResult queryCarouselInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer  pageSize);

    //获取所有商品信息
    List<AdminItemImagesInfoVo> queryAllItemInfo();

    //增加新的轮播图
    String addNewCarouselInfo(AdminCarouselBo bo, MultipartFile file);

    //根据轮播图Id获取该轮播图信息
    AdminCarouselInfoVo queryCarouselInfoById(String id);

    //修改轮播图信息
    void updateCarouselInfoById(AdminCarouselBo bo);

    //修改轮播图的图片
    void updateCarouselPicById(String id, MultipartFile file);

    //删除轮播图-暂时只做不展示处理
    void deleteCarouselById(String id);
}
