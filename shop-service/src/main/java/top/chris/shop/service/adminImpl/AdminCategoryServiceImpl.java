package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.config.SFtpConfig;
import top.chris.shop.exception.PicException;
import top.chris.shop.mapper.CategoryMapper;
import top.chris.shop.pojo.Category;
import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.bo.adminBo.AdminCategoryBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminCategoryInfoVo;
import top.chris.shop.service.admin.AdminCategoryService;
import top.chris.shop.util.FileNameUtils;
import top.chris.shop.util.PagedGridResult;
import top.chris.shop.util.SFtpUtil;

import java.util.Date;
import java.util.List;

@Log
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService
{
    @Autowired
    private CategoryMapper mapper;
    @Autowired
    private PageHelper pageHelper;
    @Autowired
    private SFtpConfig sFtpConfig;
    @Autowired
    private ShopProperties shopProperties;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryAllCategoryInfo(String condition, Integer page, Integer pageSize) {
        PagedGridResult pagedGridResult = null;
        if (condition.equals("")){
            condition = null;
            log.info("查询全部分类");
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        } else if (condition.equals("1")){
            log.info("查询一级分类:"+condition);
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else if (condition.equals("2")){
            log.info("查询二级分类:"+condition);
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else {
            log.info("查询三级分类:"+condition);
            pageHelper.startPage(page,pageSize);
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByCondition(condition);
            pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryCategoryInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (!bo.getItemName().equals("")){
            //按照商品名称查找
            pageHelper.startPage(page,pageSize);
            log.info("按照商品名称查找:"+ReflectionToStringBuilder.toString(bo));
            List<AdminCategoryInfoVo> vos = mapper.rendersCategoryByName(bo.getItemName());
            if (vos.size() == 0) {
                //查询结果为0，根据上市条件在数据库中查询不到数据
                PagedGridResult result0 = new PagedGridResult();
                result0.setRows(null);
                return result0;
            }
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
            return pagedGridResult;
        }else {
            return null;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public AdminCategoryInfoVo queryCategoryInfoById(String id) {
        return  mapper.rendersCategoryById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer addFirstCategory(AdminCategoryBo bo, MultipartFile file) {
        //查询数据库中是否已经有10个一级分类了，如果是，则无法添加新的一级分类
        int num = 0;
        List<Category> categories = mapper.selectAll();
        for (Category category : categories) {
            if (category.getFatherId() == 0){
                num += 1;
            }
        }
        if (num == 11){
            //已经有10个一级分类了,不能添加新的一级种类
            return -1;
        }
        //存储图片的地址
        String picAddressUrl = "";
        //上传结果
        String result = "---";
        //判断上传的照片是否为空
        if (file.isEmpty()) {
            throw new PicException("上传的照片为空");
        }
        try {
            //1.获取图片原来的名字
            String fileName = file.getOriginalFilename();
            //2.上传
            //2.1 通过工具类产生新图片名称，防止重名
            String picNewName = FileNameUtils.generateRandonFileName(fileName);
            //2.2 设置图片存储在服务器中的目录 例如：xx/face/userId/xxx.jpg
            String picSavePath = "foodie/category";
            log.info("----最新图片名称："+picNewName+"----最新图片存储路径："+picSavePath);
            //2.3 上传到服务器
            SFtpUtil.uploadFile(picSavePath,picNewName,file.getInputStream(),sFtpConfig);
            //3、设置本地访问服务器对应图片的地址
            picAddressUrl = sFtpConfig.getImageBaseUrl()+picSavePath+"/"+picNewName;
            log.info("浏览器访问服务器中对应图片的Url："+picAddressUrl);
        } catch (Exception e) {
            result += "上传失败，原因是："+e.getLocalizedMessage();
            log.info(result);
            e.printStackTrace();
        }
        Category category = new Category();
        category.setLogo("img/food.png"); //默认图标
        category.setSlogan(bo.getSlogan());
        category.setFatherId(0);
        category.setType(1);
        category.setBgColor(bo.getBgColor());
        category.setName(bo.getName());
        category.setCatImage(picAddressUrl);
        return mapper.insert(category);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer addOtherCategory(AdminCategoryBo bo) {
        Category category = new Category();
        category.setLogo(null);
        category.setSlogan(bo.getSlogan());
        if (bo.getType().equals("2")){//添加二级分类
            category.setFatherId(Integer.parseInt(bo.getSecCatgory()));
        }else { //添加三级分类
            category.setFatherId(Integer.parseInt(bo.getCatIdCatgory()));
        }
        category.setType(Integer.parseInt(bo.getType()));
        category.setBgColor(bo.getBgColor());
        category.setName(bo.getName());
        category.setCatImage(null);
        return mapper.insert(category);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer updateCategoryInfo(AdminCategoryBo bo) {
        Category category = mapper.selectByPrimaryKey(bo.getId());
        if (bo.getType().equals("1")){
            //修改一级分类信息
            category.setName(bo.getName());
            category.setBgColor(bo.getBgColor());
            category.setSlogan(bo.getSlogan());
        }else {
            //修改二三级分类信息
            category.setName(bo.getName());
            category.setFatherId(Integer.parseInt(bo.getFatherId()));
        }
        return mapper.updateByPrimaryKey(category);
    }

    @Override
    public Integer updateFirstCategory(String id, MultipartFile file) {
        //获取之前图片的信息
        Category category = mapper.selectByPrimaryKey(id);
        //获取之前图片的信息
        String[] split = category.getCatImage().split("/");
        log.info("图片地址切割："+ ReflectionToStringBuilder.toString(split));
        //在服务器中的目录地址
        log.info("图片在服务器中的目录地址："+split[4]);
        String directory = split[4];
        //在服务器中文件的名称
        log.info("图片在服务器中的名称："+split[5]);
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
            String picSavePath = "foodie/category";
            log.info("----最新图片名称："+picNewName+"----最新图片存储路径："+picSavePath);
            //2.4 上传到服务器
            SFtpUtil.uploadFile(picSavePath,picNewName,file.getInputStream(),sFtpConfig);
            //3、设置本地访问服务器对应图片的地址
            String picAddressUrl = sFtpConfig.getImageBaseUrl()+picSavePath+"/"+picNewName;
            log.info("浏览器访问服务器中对应图片的Url："+picAddressUrl);
            //4、更新图片的路径到数据库
            category.setCatImage(picAddressUrl);
            mapper.updateByPrimaryKey(category);
            result = "在数据库中更新成功";
            log.info(result);
        } catch (Exception e) {
            result += "上传失败，原因是："+e.getLocalizedMessage();
            log.info(result);
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminCategoryInfoVo> vos, Integer page){
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
