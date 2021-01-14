package top.chris.shop.service;

import top.chris.shop.pojo.Carousel;
import top.chris.shop.pojo.Category;
import top.chris.shop.pojo.vo.RenderSixItemsVo;
import top.chris.shop.pojo.vo.CategoryVo;

import java.util.List;

public interface IndexService {
    //轮播图的查询
    List<Carousel> rendersCarousel();
    //一级菜单栏查询
    List<Category> rendersCats();
    //二三级菜单栏查询，传入父级id
    List<CategoryVo> rendersSubCats(Integer fatherId);
    //查询所有二级分类
    List<Category> rendersAllSubCats();
    //首页每一大类对应具体商品展览，传入用户选择的一级菜单id号码
    RenderSixItemsVo rendersSubItemsCats(Integer rootCatId);
}
