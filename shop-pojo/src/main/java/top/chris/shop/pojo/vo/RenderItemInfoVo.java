package top.chris.shop.pojo.vo;

import lombok.Data;
import top.chris.shop.pojo.ItemsImg;
import top.chris.shop.pojo.ItemsParam;
import top.chris.shop.pojo.ItemsSpec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 输出到前端的商品详情页面的数据模型
 */
@Data
public class RenderItemInfoVo {
    //商品基本信息:itemName、sellCounts、content
    private SimpleItem item;
    //商品图片展示:isMain、url(一对多的映射，一个商品存在多张图片)
    private List<SimpleItemsImg> itemImgList = new ArrayList<>();
    //itemSpecList包含了ItemsSpec，即商品规格（一个商品有很多规格：龙虾有1斤、3斤等多个规格：属于一对多）:id、priceDiscount、priceNormal、stock、discounts
    private List<SimpleItemsSpec> itemSpecList = new ArrayList<>();
    //商品详情/参数：producPlace、brand、factoryName、factoryAddress、packagingMethod、footPeriod、weight、storageMethod、eatMethod
    private SimpleItemsParam itemParams;

    //使用静态内部类对数据模型进行构建
    @Data
    public static class SimpleItem{
        private String itemName;
        private Integer sellCounts;
        private String  content;
    }
    @Data
    public static class SimpleItemsImg{
        private Integer isMain;
        private String url;
    }
    @Data
    public static class SimpleItemsSpec{
        private String id;
        private String name;
        private Integer priceDiscount;
        private Integer priceNormal;
        private Integer stock;
        //确保能够准确计算精度
        private BigDecimal discounts;
    }
    @Data
    public static class SimpleItemsParam{
        private String producPlace;
        private String brand;
        private String factoryName;
        private String factoryAddress;
        private String packagingMethod;
        private String footPeriod;
        private String weight;
        private String storageMethod;
        private String eatMethod;
    }

}
