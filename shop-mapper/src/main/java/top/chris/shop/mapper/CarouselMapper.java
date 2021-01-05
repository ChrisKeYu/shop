package top.chris.shop.mapper;

import top.chris.shop.pojo.Carousel;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminCarouselInfoVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface CarouselMapper extends tk.mybatis.mapper.common.Mapper<Carousel> {
    //查询所有的轮播图信息
    List<AdminCarouselInfoVo> renderAllCategorysInfo();
    //根据条件查询轮播图信息
    List<AdminCarouselInfoVo> renderCategorysInfoByCondition(String condition, String con);
    //根据搜索条件查询轮播图信息(商品名称、三级分类查询)
    List<AdminCarouselInfoVo> renderCategorysInfoBySearch(AdminSearchItemParamBo bo);
    //按照商品名称、一级分类和三级分类查找
    List<AdminCarouselInfoVo> renderCategorysInfoByThreeSearch(AdminSearchItemParamBo bo);

}




