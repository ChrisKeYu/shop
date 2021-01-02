package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.config.SFtpConfig;
import top.chris.shop.exception.PicException;
import top.chris.shop.mapper.ItemsImgMapper;
import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemImagesInfoVo;
import top.chris.shop.service.admin.AdminItemImageService;
import top.chris.shop.util.FileNameUtils;
import top.chris.shop.util.PagedGridResult;
import top.chris.shop.util.SFtpUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log
@Service
public class AdminItemImageServiceImpl implements AdminItemImageService {
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private SFtpConfig sFtpConfig;
    @Autowired
    private ShopProperties shopProperties;
    @Autowired
    private PageHelper pageHelper;

    @Override
    public PagedGridResult queryAllItemImagesInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        log.info("查看传入的查询条件是:"+bo.getIsMain());
        List<AdminItemImagesInfoVo> vos = new ArrayList<>();
        if (bo.getIsMain() != "1"){
            pageHelper.startPage(page,pageSize);
            //查询所有信息
            log.info("查询全部:"+ReflectionToStringBuilder.toString(bo));
            vos= itemsImgMapper.queryAllItemImagesInfoByCondition(bo);
        }else if (bo.getIsMain() == "1"){
            pageHelper.startPage(page,pageSize);
            //查询isMain为1的信息（主图信息）
            log.info("按照主图查找:"+ReflectionToStringBuilder.toString(bo));
            vos = itemsImgMapper.queryAllItemImagesInfoByCondition(bo);
        }
        return finfishPagedGridResult(vos,page);
    }

    @Override
    public PagedGridResult queryItemImagesInfoByBo(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (bo.getItemName().equals("")){
            bo.setItemName(null); //方便SQL语句实现动态查询
            //只需要根据一级、二级分类条件进行查询
            if (bo.getCategory0() == 0){
                //如果一级分类也没有选择，那么就直接返回一个null，并告知：请填入任何一个搜索条件
                return null;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    bo.setCatIdCatgory0(null);//方便SQL语句实现动态查询
                    //按一级大类去查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按一级大类去查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemImagesInfoVo> vos = itemsImgMapper.queryAllItemImagesInfoByCondition(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }else {
                    //按照一级大类和三级大类去找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照一级大类和三级大类去找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemImagesInfoVo> vos = itemsImgMapper.queryAllItemImagesInfoByCondition(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }
            }
        }else {
            if (bo.getCategory0() == 0){
                bo.setCategory0(null);
                bo.setCatIdCatgory0(null);
                //按照商品名称进行模糊查询
                pageHelper.startPage(page,pageSize);
                log.info("按照商品名称进行模糊查询:"+ReflectionToStringBuilder.toString(bo));
                List<AdminItemImagesInfoVo> vos = itemsImgMapper.queryAllItemImagesInfoByItemNameAndCategory(bo);
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                return pagedGridResult;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    bo.setCatIdCatgory0(null);
                    //按照商品名称和一级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称和一级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemImagesInfoVo> vos1 = itemsImgMapper.queryAllItemImagesInfoByItemNameAndCategory(bo);
                    if (vos1.size() == 0) {
                        //查询结果为0，在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos1, page);
                    return pagedGridResult;
                }else {
                    //按照商品名称、一级分类和三级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称、一级分类和三级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemImagesInfoVo> vos1 = itemsImgMapper.queryAllItemImagesInfoByItemNameAndCategory(bo);
                    if (vos1.size() == 0) {
                        //查询结果为0，根据上市条件在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos1, page);
                    return pagedGridResult;
                }
            }
        }
    }

    @Override
    public void changeItemImgByImgId(String imageId,MultipartFile file) {
        //获取之前图片的信息
        ItemsImg itemsImg = itemsImgMapper.selectByPrimaryKey(imageId);
        //获取之前图片的信息
        String[] split = itemsImg.getUrl().split("/");
        //log.info("图片地址切割："+ ReflectionToStringBuilder.toString(split));
        //在服务器中的目录地址
        // log.info("图片在服务器中的目录地址："+split[4]);
        String directory = split[4];
        //在服务器中文件的名称
        //log.info("图片在服务器中的名称："+split[5]);
        String deleteFile = split[5];
        //上传结果
        String result = "---";
        //判断上传的照片是否为空
        if (file.isEmpty()) {
            throw new PicException("上传的照片为空");
        }
        try {
            //0 将之前的照片从服务器中删除掉
            String filePath = shopProperties.getItemImageCentosUrl()+ directory +"/"+ deleteFile;
            SFtpUtil.deleteFile(sFtpConfig,filePath);
            //1.获取图片原来的名字
            String fileName = file.getOriginalFilename();
            //2.上传
            //2.1 通过工具类产生新图片名称，防止重名
            String picNewName = FileNameUtils.generateRandonFileName(fileName);
            //2.2 设置图片存储在服务器中的目录 例如：xx/face/userId/xxx.jpg
            String picSavePath = "foodie/"+itemsImg.getItemId();
            log.info("----最新图片名称："+picNewName+"----最新图片存储路径："+picSavePath);
            //2.4 上传到服务器
            SFtpUtil.uploadFile(picSavePath,picNewName,file.getInputStream(),sFtpConfig);
            //3、设置本地访问服务器对应图片的地址
            String picAddressUrl = sFtpConfig.getImageBaseUrl()+picSavePath+"/"+picNewName;
            log.info("浏览器访问服务器中对应图片的Url："+picAddressUrl);
            //4、更新图片的路径到数据库
            itemsImg.setUrl(picAddressUrl);
            itemsImg.setUpdatedTime(new Date());
            itemsImgMapper.updateByPrimaryKey(itemsImg);
            result = "在数据库中更新成功";
            log.info(result);
        } catch (Exception e) {
            result += "上传失败，原因是："+e.getLocalizedMessage();
            log.info(result);
            e.printStackTrace();
        }
    }

    @Override
    public void deleteImageByImageId(String imageId) throws SftpException, JSchException {
        //获取之前图片的信息
        ItemsImg itemsImg = itemsImgMapper.selectByPrimaryKey(imageId);
        //获取之前图片的信息
        String[] split = itemsImg.getUrl().split("/");
        //log.info("图片地址切割："+ ReflectionToStringBuilder.toString(split));
        //在服务器中的目录地址
        // log.info("图片在服务器中的目录地址："+split[4]);
        String directory = split[4];
        //在服务器中文件的名称
        //log.info("图片在服务器中的名称："+split[5]);
        String deleteFile = split[5];
        //0 将之前的照片从服务器中删除掉
        String filePath = shopProperties.getItemImageCentosUrl()+ directory +"/"+ deleteFile;
        SFtpUtil.deleteFile(sFtpConfig,filePath);
        //从数据库中删除该商品照片数据
        itemsImgMapper.deleteByPrimaryKey(itemsImg);
        log.info("删除成功！");
    }

//    @Override
//    public ImageChangeIsMainVo queryImageInfoByImageId(String imageId) {
//        ItemsImg itemsImg = itemsImgMapper.selectByPrimaryKey(imageId);
//        ImageChangeIsMainVo vo = new ImageChangeIsMainVo();
//        vo.setIsMain(itemsImg.getIsMain());
//        vo.setItemId(itemsImg.getItemId());
//        return vo;
//    }

    @Override
    public void updateImageInfoByIdAndItemId(String itemId, String oldStatus,String isMain, String imageId) {
        //直接修改目标图片为指定的状态
        ItemsImg itemsImg = itemsImgMapper.selectByPrimaryKey(imageId);
        //先获取目标图片之前的状态
        Integer ole_status = itemsImg.getIsMain();
        itemsImg.setIsMain(Integer.parseInt(isMain));
        itemsImgMapper.updateByPrimaryKey(itemsImg);

        //在利用循环找到另外一个图片，然后对比两者的状态是否一致，如果一致就把另外一个状态修改到于目标图片状态不一致
        Example example = new Example(ItemsImg.class);
        example.createCriteria().andEqualTo("itemId",itemId);
        for (ItemsImg img : itemsImgMapper.selectByExample(example)) {
            //排除掉已经修改过的图片对象
            if (!img.getId().equals(imageId)){
                //如果未修改的图片状态等于被修改图片之前的状态，那么就把之前的状态赋值给未修改的状态
               if(img.getIsMain() != ole_status){
                   if (ole_status == 1){
                       img.setIsMain(1);
                   }else if (ole_status == 0){
                       img.setIsMain(0);
                   }

               }else {
                   //如果未修改的图片状态不等于被修改图片之前的状态，那么就把判断未修改的状态码时多少，然后赋值一个与未修改状态码不一样的值

               }
               itemsImgMapper.updateByPrimaryKey(img);
            }
        }

    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminItemImagesInfoVo> vos, Integer page){
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (vos != null || vos.size() != 0){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(vos);
            //插入查询到的指定数据，与上面传入的参数是不一致的，下面传入的参数是orders对象被pageHelper设定固定大小后返回的结果，再由返回的结果查询到的具体数据。
            pagedGridResult.setRows(vos);
            //插入当前页数
            pagedGridResult.setPage(page);
            //插入查询的总记录数
            pagedGridResult.setRecords(pageInfo.getTotal());
            //插入总页数（总记录数 / pageSize[每一页的可展示的数量]）
            pagedGridResult.setTotal(pageInfo.getPages());
        }
        return pagedGridResult;
    }



}
