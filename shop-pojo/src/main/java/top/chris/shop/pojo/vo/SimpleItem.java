package top.chris.shop.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class SimpleItem {
    /**
     * 商品名称 商品名称
     */
    @Column(name = "`item_name`")
    private String itemName;
    /**
     * 商品主键id
     */
    @Id
    @Column(name = "`id`")
    private String itemId;
    /**
     * 图片地址 图片地址
     */
    @Column(name = "`url`")
    private String itemUrl;

}
