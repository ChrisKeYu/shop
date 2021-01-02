package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemImagesInfoVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsImgMapper extends tk.mybatis.mapper.common.Mapper<ItemsImg> {
    List<RenderItemInfoVo.SimpleItemsImg> querySimpleItemsImgByItemId(String itemId);
    //管理员按条件查询所有商品的图片信息（按照是否主图、[一级分类和三级分类进行联查]）动态查询
    List<AdminItemImagesInfoVo> queryAllItemImagesInfoByCondition(AdminSearchItemParamBo bo);
    //按照商品名称和（一级分类或二级分类）进行动态查询所有商品的图片信息
    List<AdminItemImagesInfoVo> queryAllItemImagesInfoByItemNameAndCategory(AdminSearchItemParamBo bo);
}




