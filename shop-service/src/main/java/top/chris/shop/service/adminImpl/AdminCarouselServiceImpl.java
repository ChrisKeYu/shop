package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.config.SFtpConfig;
import top.chris.shop.exception.PicException;
import top.chris.shop.mapper.CarouselMapper;
import top.chris.shop.mapper.CategoryMapper;
import top.chris.shop.mapper.ItemsMapper;
import top.chris.shop.pojo.Carousel;
import top.chris.shop.pojo.Category;
import top.chris.shop.pojo.Items;
import top.chris.shop.pojo.bo.adminBo.AdminCarouselBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminCarouselInfoVo;
import top.chris.shop.pojo.vo.adminVo.AdminItemImagesInfoVo;
import top.chris.shop.service.admin.AdminCarouselService;
import top.chris.shop.util.FileNameUtils;
import top.chris.shop.util.PagedGridResult;
import top.chris.shop.util.SFtpUtil;

import java.util.*;

@Log
@Service
public class AdminCarouselServiceImpl implements AdminCarouselService  {
    @Autowired
    private CarouselMapper mapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ShopProperties shopProperties;
    @Autowired
    private SFtpConfig sFtpConfig;
    @Autowired
    private Sid sid;
    @Autowired
    private PageHelper pageHelper;

    @Override
    public PagedGridResult queryAllCarousel(String condition,Integer page, Integer pageSize) {
        PagedGridResult pagedGridResult = null;
        if (condition.equals("all")){
            log.info("查询全部轮播图");
            List<AdminCarouselInfoVo> vos = mapper.renderAllCategorysInfo();
            pagedGridResult = finfishPagedGridResult(vos, page);
        } else if (condition.equals("1:L")){
            log.info("1:L");
            List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoByCondition(condition, "1");
            if (vos.size() != 0){
                pagedGridResult = finfishPagedGridResult(vos, page);
            }
        }else if (condition.equals("2:L")){
            log.info("2:L");
            List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoByCondition(condition, "2");
            if (vos.size() != 0){
                pagedGridResult = finfishPagedGridResult(vos, page);
            }
        }else if(condition.equals("1:S")){
            log.info("1:S");
            List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoByCondition(condition, "1");
            if (vos.size() != 0){
                pagedGridResult = finfishPagedGridResult(vos, page);
            }
        }else if (condition.equals("0:S")){
            log.info("0:S");
            List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoByCondition(condition, "0");
            if (vos.size() != 0){
                pagedGridResult = finfishPagedGridResult(vos, page);
            }
        }
        return pagedGridResult;
    }

    @Override
    public PagedGridResult queryCarouselInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (bo.getItemName().equals("")){
            bo.setItemName(null); //方便SQL语句实现动态查询
            //只需要根据一级、二级分类条件进行查询
            if (bo.getCategory0() == 0){
                //如果一级分类也没有选择，那么就直接返回一个null，并告知：请填入任何一个搜索条件
                return null;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    return null;
                }else {
                    //按照一级大类和三级大类去找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照一级大类和三级大类去找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoBySearch(bo);
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
                List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoBySearch(bo);
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                return pagedGridResult;
            }else {
                //按照商品名称、一级分类和三级分类查找
                pageHelper.startPage(page,pageSize);
                log.info("按照商品名称、一级分类和三级分类查找:"+ReflectionToStringBuilder.toString(bo));
                List<AdminCarouselInfoVo> vos = mapper.renderCategorysInfoByThreeSearch(bo);
                if (vos.size() == 0) {
                    //查询结果为0，根据上市条件在数据库中查询不到数据
                    PagedGridResult result0 = new PagedGridResult();
                    result0.setRows(null);
                    return result0;
                }
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                return pagedGridResult;
            }
        }
    }

    @Override
    public List<AdminItemImagesInfoVo> queryAllItemInfo() {
        List<AdminItemImagesInfoVo> vos = new ArrayList<>();
        for (Items items : itemsMapper.selectAll()) {
            AdminItemImagesInfoVo vo = new AdminItemImagesInfoVo();
            vo.setId(items.getId());
            vo.setItemName(items.getItemName());
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public String addNewCarouselInfo(AdminCarouselBo bo, MultipartFile file) {
        String re = "---";
        //统计以下已存在的轮播图数量
        int size = mapper.selectAll().size();
        Carousel carousel = new Carousel();
        Map<String, String> map = uploadItemImgs(file,"carousel/");
        for (String s : map.keySet()) {
            if (s.equals("success")){
                if (bo.getType().equals("1")){
                    //轮播图类型为商品：点击轮播图后会跳到具体商品,此时设置三级分类为""
                    bo.setCatIdCatgory("");
                    carousel.setId(sid.nextShort());
                    carousel.setCatId(bo.getCatIdCatgory());
                    carousel.setBackgroundColor(bo.getBackgroundColor());
                    carousel.setCreateTime(new Date());
                    carousel.setImageUrl(map.get("url"));
                    carousel.setIsShow(Integer.parseInt(bo.getIsShow()));
                    carousel.setItemId(bo.getItemId());
                    carousel.setSort(size+1);
                    carousel.setType(Integer.parseInt(bo.getType()));
                    carousel.setUpdateTime(new Date());
                }else {
                    //轮播图类型为三级分类：点击轮播图后会跳到该类下面,此时设置商品id为""
                    bo.setItemId("");
                    carousel.setId(sid.nextShort());
                    carousel.setCatId(bo.getCatIdCatgory());
                    carousel.setBackgroundColor(bo.getBackgroundColor());
                    carousel.setCreateTime(new Date());
                    carousel.setImageUrl(map.get("url"));
                    carousel.setIsShow(Integer.parseInt(bo.getIsShow()));
                    carousel.setItemId(bo.getItemId());
                    carousel.setSort(size+1);
                    carousel.setType(Integer.parseInt(bo.getType()));
                    carousel.setUpdateTime(new Date());
                }
                mapper.insert(carousel);
                re += "success";
            }else {
                re += map.get("fail");
            }
        }
        return re;
    }

    @Override
    public AdminCarouselInfoVo queryCarouselInfoById(String id) {
        AdminCarouselInfoVo vo = new AdminCarouselInfoVo();
        Carousel carousel = mapper.selectByPrimaryKey(id);
        if (!carousel.getCatId().equals("")){
            //说明是分级的数据
            vo.setCatId(carousel.getCatId()); //三级
            Category category3 = categoryMapper.selectByPrimaryKey(carousel.getCatId());
            vo.setCatIdName(category3.getName());//三级名称
            //获取二级对象
            Category category2 = categoryMapper.selectByPrimaryKey(category3.getFatherId());
            vo.setSecCatgory(String.valueOf(category2.getId()));//二级
            vo.setSecCatgoryName(category2.getName());//二级名称
            //获取一级对象
            Category category1 = categoryMapper.selectByPrimaryKey(category2.getFatherId());
            vo.setCategory(String.valueOf(category1.getId()));//一级

        }else {
            vo.setCatId(carousel.getCatId()); //三级
            vo.setCatIdName("");//三级名称
            vo.setSecCatgory("");//二级
            vo.setSecCatgoryName("");//二级名称
            vo.setCategory("");//一级
        }
        vo.setId(id);
        vo.setType(carousel.getType());
        vo.setItemId(carousel.getItemId());
        vo.setIsShow(carousel.getIsShow());
        vo.setBackgroundColor(carousel.getBackgroundColor());
        return vo;
    }

    @Override
    public void updateCarouselInfoById(AdminCarouselBo bo) {
        Carousel carousel = mapper.selectByPrimaryKey(bo.getId());
        carousel.setUpdateTime(new Date());
        if (bo.getType().equals("1")){
            //此时的轮播图的类型被修改为商品
            carousel.setCatId("");
            carousel.setType(Integer.valueOf(bo.getType()));
            carousel.setItemId(bo.getItemId());
            carousel.setBackgroundColor(bo.getBackgroundColor());
            carousel.setIsShow(Integer.valueOf(bo.getIsShow()));
        }else if (bo.getType().equals("2")){
            //此时的轮播图的类型被修改为分类
            carousel.setItemId("");
            carousel.setCatId(bo.getCatIdCatgory());
            carousel.setType(Integer.valueOf(bo.getType()));
            carousel.setBackgroundColor(bo.getBackgroundColor());
            carousel.setIsShow(Integer.valueOf(bo.getIsShow()));
        }
        mapper.updateByPrimaryKey(carousel);
    }

    @Override
    public void updateCarouselPicById(String id, MultipartFile file) {
        //获取之前图片的信息
        Carousel carousel = mapper.selectByPrimaryKey(id);
        //获取之前图片的信息
        String[] split = carousel.getImageUrl().split("/");
        log.info("图片地址切割："+ ReflectionToStringBuilder.toString(split));
        //在服务器中的目录地址(轮播图不需要)
//        log.info("图片在服务器中的目录地址："+split[3]);
//        String directory = split[3];
        //在服务器中文件的名称
        log.info("图片在服务器中的名称："+split[4]);
        String deleteFile = split[4];
        //上传结果
        String result = "---";
        //判断上传的照片是否为空
        if (file.isEmpty()) {
            throw new PicException("上传的照片为空");
        }
        try {
            //1、将之前的照片从服务器中删除掉
            String filePath = shopProperties.getCarouselImageCentosUrl()+"/"+ deleteFile;
            SFtpUtil.deleteFile(sFtpConfig,filePath);
            //2、上传新的图片
            Map<String, String> map = uploadItemImgs(file,"carousel/");
            //3、更新图片的路径到数据库
            Carousel carousel1 = mapper.selectByPrimaryKey(id);
            carousel1.setImageUrl(map.get("url"));
            carousel1.setUpdateTime(new Date());
            mapper.updateByPrimaryKey(carousel1);
            result = "在数据库中更新成功";
            log.info(result);
        } catch (Exception e) {
            result += "上传失败，原因是："+e.getLocalizedMessage();
            log.info(result);
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCarouselById(String id) {
        Carousel carousel = mapper.selectByPrimaryKey(id);
        carousel.setUpdateTime(new Date());
        carousel.setIsShow(0);
        mapper.updateByPrimaryKey(carousel);
    }

    /**
     * 上传照片的方法
     * @param file 文件
     * @param picPath 存储不同类型图片的目录，Eg:(foodie-存储商品的目录图片、face-存储用户头像的目录、carousel-存储轮播图的目录)
     * @return
     */
    public Map<String, String> uploadItemImgs(MultipartFile file,String picPath){
        Map<String, String> re = new HashMap<>();
        //创建ItemsImg对象
        Carousel carousel = new Carousel();
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
            String picSavePath = picPath;
            log.info("----最新图片名称："+picNewName+"----最新图片存储路径："+picSavePath);
            //2.3 上传到服务器
            SFtpUtil.uploadFile(picSavePath,picNewName,file.getInputStream(),sFtpConfig);
            //3、设置本地访问服务器对应图片的地址
            String picAddressUrl = sFtpConfig.getImageBaseUrl()+picSavePath+picNewName;
            re.put("url",picAddressUrl);
            log.info("浏览器访问服务器中对应图片的Url："+picAddressUrl);
            result = "success";
            re.put("success",result);
            return re;
        } catch (Exception e) {
            result += "上传失败，原因是："+e.getLocalizedMessage();
            re.put("fail",result);
            e.printStackTrace();
            return re;
        }
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminCarouselInfoVo> vos, Integer page){
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
