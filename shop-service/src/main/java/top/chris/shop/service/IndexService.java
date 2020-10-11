package top.chris.shop.service;

import top.chris.shop.pojo.Carousel;
import top.chris.shop.pojo.Category;
import top.chris.shop.pojo.vo.RenderSixItemsVo;
import top.chris.shop.pojo.vo.CategoryVo;

import java.util.List;

public interface IndexService {

    List<Carousel> rendersCarousel();

    List<Category> rendersCats();

    List<CategoryVo> rendersSubCats(Integer fatherId);

    RenderSixItemsVo rendersSubItemsCats(Integer rootCatId);
}
