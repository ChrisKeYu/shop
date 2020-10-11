package top.chris.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.chris.shop.mapper.CarouselMapper;
import top.chris.shop.mapper.CategoryMapper;
import top.chris.shop.pojo.Carousel;
import top.chris.shop.pojo.Category;
import top.chris.shop.pojo.vo.RenderSixItemsVo;
import top.chris.shop.pojo.vo.CategoryVo;
import top.chris.shop.service.IndexService;

import java.util.List;
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> rendersCarousel() {
        return carouselMapper.selectAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> rendersCats() {
        Category category = new Category();
        //主种类导航栏数据查询
        category.setType(1);
        return categoryMapper.select(category);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVo> rendersSubCats(Integer fatherId) {
        return categoryMapper.rendersSubCatLazyLoad(fatherId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public RenderSixItemsVo rendersSubItemsCats(Integer rootCatId) {
        return categoryMapper.rendersSubCatItemsLazyLoad(rootCatId);
    }


}
