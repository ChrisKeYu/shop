package top.chris.shop.service.adminImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.enums.ItemStatusEnum;
import top.chris.shop.enums.OrdersStatusEnum;
import top.chris.shop.mapper.ItemsMapper;
import top.chris.shop.mapper.ItemsSpecMapper;
import top.chris.shop.mapper.OrderItemsMapper;
import top.chris.shop.mapper.OrderStatusMapper;
import top.chris.shop.pojo.Items;
import top.chris.shop.pojo.ItemsSpec;
import top.chris.shop.pojo.OrderItems;
import top.chris.shop.pojo.OrderStatus;
import top.chris.shop.pojo.bo.adminBo.AdminItemSpecBo;
import top.chris.shop.pojo.bo.adminBo.AdminSearchItemParamBo;
import top.chris.shop.pojo.vo.adminVo.AdminItemSpecInfoVo;
import top.chris.shop.service.admin.AdminItemSpecService;
import top.chris.shop.util.PagedGridResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@Service
public class AdminItemSpecServiceImpl implements AdminItemSpecService {
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private PageHelper pageHelper;

    @Override
    public PagedGridResult queryAllItemSpec(Integer page, Integer pageSize) {
        pageHelper.startPage(page,pageSize);
        List<AdminItemSpecInfoVo> vos = itemsSpecMapper.queryAllItemsSpec();
        PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
        return pagedGridResult;
    }

    @Override
    public PagedGridResult queryItemSpecInfoByCondition(AdminSearchItemParamBo bo, Integer page, Integer pageSize) {
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
                    log.info("按一级大类去查找:"+ ReflectionToStringBuilder.toString(bo));
                    List<AdminItemSpecInfoVo> vos = itemsSpecMapper.queryItemSpecByCatIdAndRootCatId(bo);
                    PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                    return pagedGridResult;
                }else {
                    //按照一级大类和三级大类去找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照一级大类和三级大类去找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemSpecInfoVo> vos = itemsSpecMapper.queryItemSpecByCatIdAndRootCatId(bo);;
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
                List<AdminItemSpecInfoVo> vos = itemsSpecMapper.queryItemSpecByCondition(bo);;
                PagedGridResult pagedGridResult = finfishPagedGridResult(vos, page);
                return pagedGridResult;
            }else {
                if (bo.getCatIdCatgory0() == 0){
                    bo.setCatIdCatgory0(null);
                    //按照商品名称和一级分类查找
                    pageHelper.startPage(page,pageSize);
                    log.info("按照商品名称和一级分类查找:"+ReflectionToStringBuilder.toString(bo));
                    List<AdminItemSpecInfoVo> vos1 = itemsSpecMapper.queryItemSpecByCondition(bo);;;
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
                    List<AdminItemSpecInfoVo> vos1 = itemsSpecMapper.queryItemSpecByCondition(bo);;;
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
    public AdminItemSpecInfoVo queryItemSpecInfoBySpecId(String specId) {
        ItemsSpec itemsSpec = itemsSpecMapper.selectByPrimaryKey(specId);
        AdminItemSpecInfoVo vo = new AdminItemSpecInfoVo();
        vo.setName(itemsSpec.getName());
        vo.setStock(itemsSpec.getStock());
        vo.setDiscounts(itemsSpec.getDiscounts());
        vo.setPriceNormal(itemsSpec.getPriceNormal());
        return vo;
    }

    @Override
    public void updateItemSpecInfoBySpecId(AdminItemSpecBo bo) {
        ItemsSpec itemsSpec = itemsSpecMapper.selectByPrimaryKey(bo.getId());
        itemsSpec.setUpdatedTime(new Date());
        itemsSpec.setPriceDiscount(bo.getPriceNormal());
        itemsSpec.setPriceNormal(bo.getPriceNormal());
        itemsSpec.setDiscounts(bo.getDiscounts());
        itemsSpec.setStock(bo.getStock());
        itemsSpecMapper.updateByPrimaryKey(itemsSpec);
    }

    @Override
    public Map<String, String> deleteItemSpecInfoBySpecId(String specId,String itemId) {
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
            ItemsSpec itemsSpec = itemsSpecMapper.selectByPrimaryKey(specId);
            if (itemsSpec != null){ //商品规格表中存在这些商品规格
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
                //库存为0且所有订单中于该商品有关的订单都除于交易完成的状态时，可以删除商品规格
                if (itemsSpec.getStock() == 0 && orderIsSuccess) {
                    log.info("商品规格Id："+itemsSpec.getId()+"，的库存为0，且在所有订单中的状态都为："+orderIsSuccess+"，此时执行删除该商品规格操作！");
                    //该商品旗下的某一个规格的库存如果为0，那么可以删除该旗下的规格商品
                    itemsSpecMapper.delete(itemsSpec);
                    res.put("success","success");
                }else {
                    //记录下哪些商品的规格不为零
                    itemIds += "商品规格ID为"+itemsSpec.getId()+":库存还有"+itemsSpec.getStock()+" 个"+";";
                    log.info("记录下哪些商品规格的库存不为零："+itemIds);
                    res.put("fail",itemIds+"----"+orderResult);
                }
            }else {
                //该商品旗下的某一个规格的库存如果不为0，那么不可以删除该旗下的规格商品
            }
        }else {
            result = "商品ID为："+itemId+",的商品还没有下架，不可以做删除操作";
            res.put("fail",result);
        }
        return res;
    }

    /**
     * 将分页查询的结果进行封装
     * @param vos 数据库取出来后再一次封装好的结果
     * @param page
     * @return
     */
    public PagedGridResult finfishPagedGridResult(List<AdminItemSpecInfoVo> vos, Integer page){
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
