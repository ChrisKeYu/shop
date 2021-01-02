package top.chris.shop.service.admin;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.util.PagedGridResult;

public interface AdminItemImageService {

    //多条件查询所有商品的图片信息
    PagedGridResult queryAllItemImagesInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize);

    //多条件查询指定商品的图片信息
    PagedGridResult queryItemImagesInfoByBo(AdminSearchItemParamBo bo, Integer page, Integer pageSize);

    //根据图片Id更换图片
    void changeItemImgByImgId(String imageId, MultipartFile file);

    //根据图片Id删除图片
    void deleteImageByImageId(String imageId) throws SftpException, JSchException;

//    //根据图片id查询图片信息
//    ImageChangeIsMainVo queryImageInfoByImageId(String imageId);

    //根据图片id和对应的itemid修改图片信息
    void updateImageInfoByIdAndItemId(String itemId, String oldStatus,String isMain,String imageId);
}
