package top.chris.shop.mapper;

import top.chris.shop.pojo.Category;
import top.chris.shop.pojo.vo.RenderSixItemsVo;
import top.chris.shop.pojo.vo.CategoryVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface CategoryMapper extends tk.mybatis.mapper.common.Mapper<Category> {
    public List<CategoryVo> rendersSubCatLazyLoad(Integer rootCatId);

    /**
     * 一种类别，对多个商品。所以返回只有一个类别Vo，但是该返回对象中又存在一个List集合存储多个商品item。
     * 这种方法其实就是通过前端的滚动下滑到下一个种类的时候就开始调用后台进行查询该种类下的多个商品，也就是1对多。实现类懒加载
     * @param rootCatId
     * @return
     */
    public RenderSixItemsVo rendersSubCatItemsLazyLoad(Integer rootCatId);
}




