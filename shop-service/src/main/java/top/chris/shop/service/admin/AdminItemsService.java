package top.chris.shop.service.admin;


import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.*;
import top.chris.shop.pojo.vo.adminVo.AdminItemImgsVo;
import top.chris.shop.pojo.vo.adminVo.ItemsInfoVo;
import top.chris.shop.util.PagedGridResult;

import java.util.List;
import java.util.Map;

public interface AdminItemsService {
    //多条件查询所有Items信息
    PagedGridResult queryAllItemsByCondition(String condition,Integer page, Integer pageSize);
    //根据ItemId插入商品参数到数据库中
    Integer insertItemParamByItemId(AdminItemParamBo bo);
    //添加新的商品
    Integer insertItem(AdminItemBo bo);
    //多条件查询Item信息
    PagedGridResult queryItemsByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize);
    //根据ItemId添加商品规格
    Map<Integer, String> insertItemSpec(AdminItemSpecBo bo);
    //根据传入的参数和MultipartFile对象集合，将商品照片存入到服务器，地址存储到数据库中
    void insertItemImgs(String itemId, List<MultipartFile> files);
    //根据ItemId查询商品照片
    List<AdminItemImgsVo> queryItemImgsByItemId(String itemId);
    //根据ItemId查询商品信息
    ItemsInfoVo queryItemInfoByItemId(String itemId);
    //根据ItemId修改商品信息
    Integer updateItemInfo(AdminItemBo bo);
    //根据ItemId删除商品
    Map<String,String> deleteItemByItemId(String itemId);
    //TODO (无法删除，原因未知) 根据商品图片的ID删除图片
    Integer deleteItemImgById(String id,String directory,String deleteFile);
}
