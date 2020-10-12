package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsComments;
import top.chris.shop.pojo.dto.ItemCommentLevelDto;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsCommentsMapper extends tk.mybatis.mapper.common.Mapper<ItemsComments> {
    //通过传入商品id参数，获取该商品的每一种类型的评论数量(一种商品可能好、中、坏三种评论都有人/无人评价，因此返回的对象是多个或者0个)
    public List<ItemCommentLevelDto> getCommentsForEveryLevelByItemId(String ItemId);
}




