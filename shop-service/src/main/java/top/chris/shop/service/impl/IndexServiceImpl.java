package top.chris.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
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

    /**
     * 轮播图的查询
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> rendersCarousel() {
        Example example = new Example(Carousel.class);
        example.createCriteria().andEqualTo("isShow","1");
        return carouselMapper.selectByExample(example);
    }

    /**
     * 一级菜单栏查询
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> rendersCats() {
        Category category = new Category();
        //主种类导航栏数据查询
        category.setType(1);
        return categoryMapper.select(category);
    }

    /**
     * 二三级菜单栏查询，传入父级id
     * @param fatherId 传入父级id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVo> rendersSubCats(Integer fatherId) {
        return categoryMapper.rendersSubCatLazyLoad(fatherId);
    }

    @Override
    public List<Category> rendersAllSubCats() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("type","2");
        return categoryMapper.selectByExample(example);
    }

    /**
     * 首页每一大类对应具体商品展览，传入用户选择的一级菜单id号码
     * @param rootCatId 传入用户选择的一级菜单id号码
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public RenderSixItemsVo rendersSubItemsCats(Integer rootCatId) {
        return categoryMapper.rendersSubCatItemsLazyLoad(rootCatId);
    }


}
