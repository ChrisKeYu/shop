package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcraft.jsch.JSchException;
import lombok.extern.java.Log;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.config.SFtpConfig;
import top.chris.shop.enums.ItemStatusEnum;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.exception.PicException;
import top.chris.shop.mapper.*;
import top.chris.shop.pojo.*;
import top.chris.shop.pojo.bo.adminBo.*;
import top.chris.shop.pojo.vo.adminVo.AdminItemImgsVo;
import top.chris.shop.pojo.vo.adminVo.IsExistVo;
import top.chris.shop.pojo.vo.adminVo.ItemsInfoVo;
import top.chris.shop.service.admin.AdminItemsService;
import top.chris.shop.util.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@Service
public class AdminItemsServiceImpl implements AdminItemsService {
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ItemsParamMapper paramMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private SFtpConfig sFtpConfig;
    @Autowired
    private Sid sid;
    @Autowired
    private PageHelper pageHelper;

    /**
     *  多条件查询所有Items信息
     * @param condition 查询条件：''默认查询全部，‘y’只查看上架的商品，‘n’只查看下架商品，‘c’默认按销量排序。
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryAllItemsByCondition(String condition, Integer page, Integer pageSize) {
        log.info("查看传入的查询条件是:"+condition);
        if (condition == ""){
            //查询所有
            pageHelper.startPage(page,pageSize);
            List<Items> items = itemsMapper.selectAll();
            List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page,items);
            return pagedGridResult;
        }else if (condition.equals("y")){
            //只查询上架的商品
            Example example = new Example(Items.class);
            example.createCriteria().andEqualTo("onOffStatus","1");
            pageHelper.startPage(page,pageSize);
            List<Items> items = itemsMapper.selectByExample(example);
            List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page,items);
            return pagedGridResult;
        }else if (condition.equals("n")){
            //只查询下架的商品
            Example example = new Example(Items.class);
            example.createCriteria().andEqualTo("onOffStatus","2");
            pageHelper.startPage(page,pageSize);
            List<Items> items = itemsMapper.selectByExample(example);
            List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page,items);
            return pagedGridResult;
        }else{
            //按销量降序排列查询
            Example example = new Example(Items.class);
            example.setOrderByClause("sell_counts DESC");
            pageHelper.startPage(page,pageSize);
            List<Items> items = itemsMapper.selectByExample(example);
            List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
            PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page,items);
            return pagedGridResult;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer insertItemParamByItemId(AdminItemParamBo bo) {
        Integer result = 0;
        Example example = new Example(ItemsParam.class);
        example.createCriteria().andEqualTo("itemId",bo.getItemId());
        //判断数据库中是否已经存在了该ItemId下的商品参数数据，存在则不可以再次添加，只能修改
        for (ItemsParam itemsParam : paramMapper.selectByExample(example)) {
            if (itemsParam.getItemId().equals(bo.getItemId())){
                return result;
            }
        }
        ItemsParam itemsParam = new ItemsParam();
        itemsParam.setId(bo.getItemId()+"-"+"param");
        itemsParam.setItemId(bo.getItemId());
        itemsParam.setBrand(bo.getBrand());
        itemsParam.setWeight(bo.getWeight());
        itemsParam.setEatMethod(bo.getEatMethod());
        itemsParam.setFactoryAddress(bo.getFactoryAddress());
        itemsParam.setFactoryName(bo.getFactoryName());
        itemsParam.setFootPeriod(bo.getFootPeriod());
        itemsParam.setPackagingMethod(bo.getPackagingMethod());
        itemsParam.setProducPlace(bo.getProducPlace());
        itemsParam.setStorageMethod(bo.getStorageMethod());
        itemsParam.setCreatedTime(new Date());
        itemsParam.setUpdatedTime(new Date());
        result = paramMapper.insert(itemsParam);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer insertItem(AdminItemBo bo) {
        //获取所有商品表的数据,查看是新添加数据中的商品名称是否在数据库中已经存在
        for (Items items : itemsMapper.selectAll()) {
            if ( items.getItemName().equals(bo.getItemName())){
                return 500;
            }
        }
        Items items = new Items();
        items.setId(sid.nextShort());
        items.setItemName(bo.getItemName());
        items.setRootCatId(bo.getRootCatId());
        items.setCatId(bo.getCatId());
        items.setOnOffStatus(bo.getOnOffStatus());
        items.setSellCounts(0);
        items.setContent("<p>"+bo.getContent()+"</p>");
        items.setCreatedTime(new Date());
        items.setUpdatedTime(new Date());
        return itemsMapper.insert(items);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryItemsByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
        if (bo.getItemName().equals("")){
            //只需要根据一级、二级分类条件进行查询
            if (bo.getCategory0() == 0){
                //如果一级分类也没有选择，那么就直接返回一个null，并告知：请填入任何一个搜索条件
                return null;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    //按一级大类去查找
                    Example example = new Example(Items.class);
                    example.createCriteria().andEqualTo("rootCatId",bo.getCategory0());
                    pageHelper.startPage(page,pageSize);
                    List<Items> items = itemsMapper.selectByExample(example);
                    List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page, items);
                    return pagedGridResult;
                }else {
                    //按照一级大类和二级大类去找
                    Example example = new Example(Items.class);
                    example.createCriteria().andEqualTo("rootCatId",bo.getCategory0()).andEqualTo("catId",bo.getCatIdCatgory0());
                    pageHelper.startPage(page,pageSize);
                    List<Items> items = itemsMapper.selectByExample(example);
                    List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page, items);
                    return pagedGridResult;
                }
            }
        }else {
            if (bo.getCategory0() == 0){
                //按照商品名称进行模糊查询
                Example example = new Example(Items.class);
                example.createCriteria().andLike("itemName","%"+bo.getItemName()+"%");
                pageHelper.startPage(page,pageSize);
                List<Items> items = itemsMapper.selectByExample(example);
                List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page, items);
                return pagedGridResult;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    //按照商品名称和一级分类查找
                    Example example = new Example(Items.class);
                    example.createCriteria().andEqualTo("rootCatId",bo.getCategory0()).andLike("itemName","%"+bo.getItemName()+"%");
                    pageHelper.startPage(page,pageSize);
                    List<Items> items = itemsMapper.selectByExample(example);
                    if (items.size() == 0) {
                        //查询结果为0，根据上市条件在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page, items);
                    return pagedGridResult;
                }else {
                    //按照商品名称、一级分类和二级分类查找
                    Example example = new Example(Items.class);
                    example.createCriteria().andEqualTo("rootCatId",bo.getCategory0())
                            .andEqualTo("catId",bo.getCatIdCatgory0())
                            .andLike("itemName","%"+bo.getItemName()+"%");
                    List<Items> items = itemsMapper.selectByExample(example);
                    if (items.size() == 0) {
                        //查询结果为0，根据上市条件在数据库中查询不到数据
                        PagedGridResult result0 = new PagedGridResult();
                        result0.setRows(null);
                        return result0;
                    }
                    List<ItemsInfoVo> vos = finfishItemsInfoVo(items);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page, items);
                    return pagedGridResult;
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Map<Integer, String> insertItemSpec(AdminItemSpecBo bo) {
        Map<Integer, String> map = new HashMap<>();
        Example example = new Example(ItemsSpec.class);
        example.createCriteria().andEqualTo("itemId",bo.getItemId());
        List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(example);
        for (ItemsSpec itemsSpec : itemsSpecs) {
            if (itemsSpec.getName().equals(bo.getName())){
                map.put(0,"不可以添加规格名称为"+bo.getName()+"的规格商品，因为该规格的商品已经存在于数据库中");

            }
        }
        ItemsSpec itemsSpec = new ItemsSpec();
        itemsSpec.setId(sid.nextShort());
        itemsSpec.setItemId(bo.getItemId());
        itemsSpec.setStock(bo.getStock());
        itemsSpec.setName(bo.getName());
        itemsSpec.setDiscounts(bo.getDiscounts());
        itemsSpec.setPriceNormal(bo.getPriceNormal());
        //BigDecimal数据类型与int类型数据进行相乘的过程
        BigDecimal discounts = bo.getDiscounts();
        int priceNormal = bo.getPriceNormal();
        BigDecimal answer = discounts.multiply(new BigDecimal(priceNormal));
        //BigDecimal 转换成 int数据类型
        itemsSpec.setPriceDiscount(answer.intValue());
        itemsSpec.setCreatedTime(new Date());
        itemsSpec.setUpdatedTime(new Date());
        map.put(itemsSpecMapper.insert(itemsSpec)," ");
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void insertItemImgs(String itemId, List<MultipartFile> files) {
        for (int i = 0;i < files.size();i++){
            //使用默认的isMain和sort
            uploadItemImgs( files.get(i), itemId,i,i);

        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AdminItemImgsVo> queryItemImgsByItemId(String itemId) {
        Example example = new Example(ItemsImg.class);
        example.createCriteria().andEqualTo("itemId",itemId);
        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example);
        List<AdminItemImgsVo> vos = new ArrayList<>();
        for (ItemsImg itemImg : itemsImgs) {
            AdminItemImgsVo vo = new AdminItemImgsVo();
            vo.setId(itemImg.getId());
            String url = itemImg.getUrl();
            vo.setImgUrl(url);
            String[] split = url.split("/");
            //log.info("图片地址切割："+ ReflectionToStringBuilder.toString(split));
            //在服务器中的目录地址
            // log.info("图片在服务器中的目录地址："+split[4]);
            vo.setDirectory(split[4]);
            //在服务器中文件的名称
            //log.info("图片在服务器中的名称："+split[5]);
            vo.setDeleteFile(split[5]);
            vos.add(vo);
        }
        return vos;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsInfoVo queryItemInfoByItemId(String itemId) {
        Items items = itemsMapper.selectByPrimaryKey(itemId);
        ItemsInfoVo vo = new ItemsInfoVo();
        vo.setItemName(items.getItemName());
        //使用正则表达式获取指定内容
        String regex = "<p>(.*?)</p>";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(items.getContent());
        //List<String> list = new ArrayList<String>();
        while (m.find()) {
            int i = 1;
            vo.setContent(m.group(i));
            //i++;
        }
        log.info("从数据库获取的商品信息"+vo.getContent());
        vo.setOnOffStatus(items.getOnOffStatus());
        vo.setCatId(items.getCatId());
        vo.setRootCatId(items.getRootCatId());
        Category category = categoryMapper.selectByPrimaryKey(items.getCatId());
        vo.setSecCatgory(category.getFatherId());
        vo.setSecCatgoryName(category.getName());
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("fatherId",category.getFatherId());
        List<Category> categories = categoryMapper.selectByExample(example);
        vo.setCatIdName(categories.get(0).getName());
        return vo;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer updateItemInfo(AdminItemBo bo) {
        Items items = itemsMapper.selectByPrimaryKey(bo.getItemId());
        items.setUpdatedTime(new Date());
        items.setContent("<p>"+bo.getContent()+"</p>");
        items.setOnOffStatus(bo.getOnOffStatus());
        items.setRootCatId(bo.getRootCatId());
        items.setCatId(bo.getCatId());
        items.setItemName(bo.getItemName());
        return itemsMapper.updateByPrimaryKey(items);
    }

    @Override
    public Map<String,String> deleteItemByItemId(String itemId) {
        Map<String,String> res = new HashMap<>();
        String result = "";
        String orderResult = " "; //订单方面的检测结果
        String orderId = "";
        //用于标记，订单中是否存在该规格的商品，如果存在且订单已完成则为true，如果存在且订单为未完成则为false，如果不存在则为true
        boolean orderIsSuccess = false;
        Items items = itemsMapper.selectByPrimaryKey(itemId);
        //判断该商品是否已经下架(所有商品及旗下的规格，都是在下架和订单状态为success的情况下才考虑删除操作)
        if (items.getOnOffStatus() == ItemStatusEnum.OFF.type){
            //1、先检查ItemId下的商品规格是否存在，存在且这些商品规格的库存都不为0，不可以执行删除，存在商品规格且库存都为0就可以删除掉这些商品规格
            Example itemSpecexample = new Example(ItemsSpec.class);
            itemSpecexample.createCriteria().andEqualTo("itemId",items.getId());
            List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(itemSpecexample);
            if (itemsSpecs.size() != 0){ //商品规格表中存在这些商品规格
                //2、判断订单下面是否有商品ItemId(只要有商品的Id，无论顾客买什么该商品下的规格都认为是订单中存在该商品)，如果有且订单除于未完成状态也不可以删除
                Example example = new Example(OrderItems.class);
                example.createCriteria().andEqualTo("itemId",itemId);
                List<OrderItems> orderItems = orderItemsMapper.selectByExample(example); //根据商品ItemId获取对应订单中的商品条目(order_items表格)中的订单ID信息
                if (orderItems.size() != 0){
                    //说明该商品出现在某一个订单中，还得检查该订单的状态
                    String allOrderIsSuccess = " "; //用于记录每一个包含该商品规格的订单的交易状态
                    for (OrderItems orderItem : orderItems) {
                        //获取对应商品Id的订单编号
                        orderId = orderItem.getOrderId();
                        //根据订单Id，去订单状态表中查询该订单的交易状态
                        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
                        //判断该订单的交易状态是否为Success
                        if (orderStatus.getOrderStatus() == OrdersStatusEnum.SUCCESS.type){
                            allOrderIsSuccess += "success,";
                        }else {
                            //只要在任何未交易成功的订单中出现该ID的商品，就不可以执行删除操作
                            orderResult = "商品ID为："+itemId+"，的商品不能进行删除操作！因为该商品在订单为："+orderId+"，的交易状态为未完成状态";
                            allOrderIsSuccess += "fail";
                        }
                    }
                    if (allOrderIsSuccess.contains("fail")){ //包含fail证明该商品ID，在订单中还是存在有未完成交易的情况，此时不可以删除
                        orderIsSuccess = false;
                        log.info("订单中还是存在有未完成交易的情况，此时不可以删除:"+orderIsSuccess);
                    }else {
                       //所有包含该规格商品的订单都是已经完成了的
                        orderIsSuccess = true;
                        log.info("所有包含该商品(ItemId)的订单都是已经完成状态："+orderIsSuccess);

                    }
                }else {
                    //说明该商品没有存在于任何一个订单中
                    orderIsSuccess = true;
                    log.info("说明该商品(ItemId)没有存在于任何一个订单中:"+orderIsSuccess);
                }

                //3、该商品旗下有其它商品规格，需要逐一判断商品规格的库存是否为0，如果为0那么可以删除掉该商品规格信息
                String itemIds = ""; //ItemSpec表格检测的结果
                for (ItemsSpec itemsSpec : itemsSpecs) {
                    //库存为0且所有订单中于该商品有关的订单都除于交易完成的状态时，可以删除商品规格
                    if (itemsSpec.getStock() == 0 && orderIsSuccess) {
                        log.info("商品规格Id："+itemsSpec.getId()+"，的库存为0，且在所有订单中的状态都为："+orderIsSuccess+"，此时执行删除该商品规格操作！");
                        //该商品旗下的某一个规格的库存如果为0，那么可以删除该旗下的规格商品
                        itemsSpecMapper.delete(itemsSpec);
                        //再次查询ItemSpec表，看看是否还有该商品ID旗下的规格商品，如果为空，那么就可以进行该商品的删除包括：参数、图片
                        Example example1 = new Example(ItemsSpec.class);
                        example1.createCriteria().andEqualTo("itemId",itemId);
                        if (itemsSpecMapper.selectByExample(example1).size() == 0){
                            log.info("删除完商品规格ID为："+itemsSpec.getId()+"的商品规格后，再次循环ItemSpec表，检测是否还有商品ItemId为："+itemId+"的旗下商品规格？");
                            res.put("success",delItemAllInfo(itemId, items));
                            log.info("ItemSpec表中已经没有了商品ItemId为："+itemId+"的旗下商品规格,可以删除该商品的所有信息包括：参数、图片和自身item");
                            break;
                        }
                        log.info("ItemSpec表中依然还有商品ItemId为："+itemId+"的旗下商品规格,暂时还不可以删除该商品，需要等到旗下所有商品规格都先被删除完毕后才可以执行删除该商品的所有信息");
                    }else {
                        //记录下哪些商品的规格不为零
                        itemIds += "商品规格ID为"+itemsSpec.getId()+":库存还有"+itemsSpec.getStock()+" 个"+";";

                    }
                }
                log.info("记录下哪些商品规格的库存不为零："+itemIds);
                //该商品旗下的某一个规格的库存如果不为0，那么不可以删除该旗下的规格商品
                res.put("fail",itemIds+"----"+orderResult);
            }else {
                //商品规格表中不存在该商品的规格，因为只要商品规格表中不存在该商品规格，那么顾客是不可能买到该商品规格，因此订单中肯定没有该规格商品信息，那么可以执行删除商品的操作
                res.put("success",delItemAllInfo(itemId, items));
            }
        }else {
            result = "商品ID为："+itemId+",的商品还没有下架，不可以做删除操作";
            res.put("fail",result);
        }
        return res;
    }

    //TODO (无法删除，原因未知)
    @Override
    public Integer deleteItemImgById(String id,String directory,String deleteFile) {
        //从数据库中删除存储在服务器中的url地址
//        int i = itemsImgMapper.deleteByPrimaryKey(id);
//        if (i != 0){
        //从服务器中删除照片
        deleteItemImgs(directory,deleteFile);
//        }
        return 1;
    }

    /**
     * 封装数据库中查询到的信息到Vo对象中，并且返回Vo对象
     * @param items 数据库中查询的结果
     * @return  返回数据库中返回结果的封装结果
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ItemsInfoVo> finfishItemsInfoVo(List<Items> items){
        List<ItemsInfoVo> vos = new ArrayList<>();
        for (Items item : items) {
            ItemsInfoVo vo = new ItemsInfoVo();
            vo.setId(item.getId());
            vo.setItemName(item.getItemName());
            vo.setContent(item.getContent());
            vo.setOnOffStatus(item.getOnOffStatus());
            vo.setSellCounts(Integer.toString(item.getSellCounts()));
            vo.setCreatedTime(item.getCreatedTime());
            vo.setUpdatedTime(item.getUpdatedTime());
            int catId = item.getCatId();
            vo.setCatName(categoryMapper.selectByPrimaryKey(catId).getName());
            //判断数据库中是否已经存在了商品的规格、参数、照片数据，如果存在那么在前端就不展示接口。
            IsExistVo existVo = queryIsExistByItemId(item.getId());
            vo.setSpecIsExist(existVo.getSpecIsExist());
            vo.setParamIsExist(existVo.getParamIsExist());
            vo.setImgIsExist(existVo.getImgIsExist());
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<ItemsInfoVo> vos, Integer page,List<Items> items){
        //使用分页插件
        PagedGridResult pagedGridResult = new PagedGridResult();
        if (vos != null){
            //传入的参数是被pageHelper设定大小查询返回的结果对象，因为被pageHelper设定后，它会先去查询总量，然后再根据你指定的大小输出具体量的数据,因此总的记录数是保存在该对象上的
            PageInfo<?> pageInfo = new PageInfo<>(items);
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

    /**
     * 根据ItemId查询在三个表中是否有对应数据
     * @param itemId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public IsExistVo queryIsExistByItemId(String itemId) {
        IsExistVo vo = new IsExistVo();
        //1、查询Param表中是否存在
        Example example1 = new Example(ItemsParam.class);
        example1.createCriteria().andEqualTo("itemId",itemId);
        List<ItemsParam> itemsParams = paramMapper.selectByExample(example1);
        if (itemsParams.size() == 0){
            vo.setParamIsExist(false);
        }else {
            vo.setParamIsExist(true);
        }
        //2、查询Spec表中是否存在
        Example example2 = new Example(ItemsSpec.class);
        example2.createCriteria().andEqualTo("itemId",itemId);
        List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(example2);
        if (itemsSpecs.size() < 4){ //商品的规格暂时只能有三种，不能超过三种
            vo.setSpecIsExist(false);
        }else {
            vo.setSpecIsExist(true);
        }
        //3、查询Img表中是否存在
        Example example3 = new Example(ItemsImg.class);
        example3.createCriteria().andEqualTo("itemId",itemId);
        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example3);
        if (itemsImgs.size() < 3){ //商品的照片暂时只能上传两张，不能超过两张
            vo.setImgIsExist(false);
        }else {
            vo.setImgIsExist(true);
        }
        return vo;
    }

    /**
     * 上传照片的方法
     * @param file 照片
     * @param itemId 商品id
     * @param isMain 主图设置
     * @param sort   图片排序
     */
    public void uploadItemImgs(MultipartFile file, String itemId,Integer isMain, Integer sort){
        //创建ItemsImg对象
        ItemsImg itemsImg = new ItemsImg();
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
            String picSavePath = "foodie/"+itemId;
            log.info("----最新图片名称："+picNewName+"----最新图片存储路径："+picSavePath);
            //2.3 上传到服务器
            SFtpUtil.uploadFile(picSavePath,picNewName,file.getInputStream(),sFtpConfig);
            //3、设置本地访问服务器对应图片的地址
            String picAddressUrl = sFtpConfig.getImageBaseUrl()+picSavePath+"/"+picNewName;
            log.info("浏览器访问服务器中对应图片的Url："+picAddressUrl);
            //4、保存图片的路径到数据库
            itemsImg.setUrl(picAddressUrl);
            itemsImg.setId(sid.nextShort());
            itemsImg.setItemId(itemId);
            itemsImg.setCreatedTime(new Date());
            itemsImg.setUpdatedTime(new Date());
            itemsImg.setIsMain(isMain);
            itemsImg.setSort(sort);
            itemsImgMapper.insert(itemsImg);
            result = "上传成功";
            log.info(result);
        } catch (Exception e) {
            result += "上传失败，原因是："+e.getLocalizedMessage();
            log.info(result);
            e.printStackTrace();
        }
    }

    /**
     * 删除Item商品信息包括：商品、商品参数、商品规格和商品照片
     * @param itemId 商品id
     * @param items 商品
     * @return
     */
    public String delItemAllInfo(String itemId,Items items){
        String result = "";
        //该商品旗下没有其它商品规格，那么可以删除商品信息、商品参数和商品照片
        //1、删除照片(只是从数据库中删除，没有从服务器中删除照片)
        Example example2 = new Example(ItemsImg.class);
        example2.createCriteria().andEqualTo("itemId",itemId);
        int imgDel = 0;
        if (itemsImgMapper.selectByExample(example2).size() == 0){
            //数据库中没有存在该商品的图片,那么就不用删除它，默认设置imgDel为1
            imgDel = 1;
        }else {
            imgDel = itemsImgMapper.deleteByExample(example2);
        }
        //2、删除商品参数
        Example example3 = new Example(ItemsParam.class);
        example3.createCriteria().andEqualTo("itemId",itemId);
        int paramDel = 0;
        if (paramMapper.selectByExample(example3).size() == 0){
            paramDel = 1 ;
        }else {
            paramDel = paramMapper.deleteByExample(example3);
        }
        //3、删除item
        int delete = itemsMapper.delete(items);
        if(imgDel != 0 && paramDel != 0 && delete != 0){
            result = "成功的将商品ID为："+itemId+"，的商品删除，执行包括了对应商品的规格、商品的参数和商品的图片数据";
        }
        return result;
    }

    //TODO (无法删除，原因未知)
    /**
     * 删除服务器中存储的商品照片
     * @param directory 目录
     * @param deleteFile 文件名
     */
    public void deleteItemImgs(String directory,String deleteFile){
        String picSavePath = "foodie/"+directory;
        String fileName = deleteFile;
        try {
            SFtpUtil.delete(picSavePath,fileName,sFtpConfig);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
}
