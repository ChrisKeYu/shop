package top.chris.shop.service.admin;

import top.chris.shop.pojo.bo.adminBo.AdminItemSpecBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemCommentInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemSpecInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.Map;

public interface AdminItemCommentService {

    //查询所有商品评论的信息
    PagedGridResult queryAllItemComment(String condition,Integer page,Integer pageSize);

    //根据多条件查询ItemParamInfo
    PagedGridResult queryItemCommentInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize);

    //根据SpecId删除对应的视频规格
    String deleteItemCommentInfoBySpecId(String commentID);
}
