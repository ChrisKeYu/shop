package top.chris.shop.mapper;

import top.chris.shop.pojo.ItemsComments;
import top.chris.shop.pojo.bo.CommentBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.dto.ItemCommentLevelDto;
import top.chris.shop.pojo.vo.CommentRecordVo;
import top.chris.shop.pojo.vo.MyCommentVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemCommentInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemParamVo;

import java.util.List;

/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
public interface ItemsCommentsMapper extends tk.mybatis.mapper.common.Mapper<ItemsComments> {
    //通过传入商品id参数，查询该商品的每一种类型的评论数量(一种商品可能好、中、坏三种评论都有人/无人评价，因此返回的对象是多个或者0个)
     List<ItemCommentLevelDto> getCommentForEveryLevelByItemId(String ItemId);

    //通过传入商品id参数和评论级别参数，查询该商品对应参数级别的评论内容
     List<CommentRecordVo> getCommentByItemIdAndLevel(CommentBo commentBo);

    //查询所有商品评论
    List<AdminItemCommentInfoVo> getAllAdminItemCommentInfo(String condition);

    //根据商品名称、用户名称、一级和三级查询商品的参数信息
    List<AdminItemCommentInfoVo> queryItemCommentByCondition(AdminSearchItemParamBo bo);

    //根据一级和三级查询具体商品的参数信息
    List<AdminItemCommentInfoVo> queryItemCommentByCatIdAndRootCatId(AdminSearchItemParamBo bo);

    //根据商品名称和用户名称进行模糊查询以及一级和三级查询商品的参数信息
    List<AdminItemCommentInfoVo> queryItemCommentByUserNameAndItemNameAndCat(AdminSearchItemParamBo bo);

    //根据UserId查询该用户下的所有订单
    List<MyCommentVo> queryItemCommentByUserId(String userId);
}




