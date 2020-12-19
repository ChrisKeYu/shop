package top.chris.shop.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

//购物项数据:用于描述一个商品在购物车的数据展示
@Table(name = "`cart_item`")
public class CartItem implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`cartId`")
    private String cartId;

    @Column(name = "`userId`")
    private String userId;

    @Column(name = "`itemId`")
    private String itemId;

    @Column(name = "`itemImgUrl`")
    private String itemImgUrl;

    @Column(name = "`itemName`")
    private String itemName;

    @Column(name = "`specId`")
    private String specId;

    @Column(name = "`specName`")
    private String specName;

    @Column(name = "`priceDiscount`")
    private Integer priceDiscount;

    @Column(name = "`priceNormal`")
    private Integer priceNormal;

    @Column(name = "`buyCounts`")
    private Integer buyCounts;

    /**
     * 计算单个购物项价格（小计）：优惠价格*购买数量,(因为管理后台在上架商品的时候会将原价和折扣输入，
     *                            然后自动计算优惠价格，最后写入数据库，因此这里直接取优惠价格计算)
     * @return
     */
    public Integer getCartItemSubTotal(){
        return priceDiscount*buyCounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemImgUrl() {
        return itemImgUrl;
    }

    public void setItemImgUrl(String itemImgUrl) {
        this.itemImgUrl = itemImgUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpecId() {
        return specId;
    }

    public void setSpecId(String specId) {
        this.specId = specId;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(Integer priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public Integer getPriceNormal() {
        return priceNormal;
    }

    public void setPriceNormal(Integer priceNormal) {
        this.priceNormal = priceNormal;
    }

    public Integer getBuyCounts() {
        return buyCounts;
    }

    public void setBuyCounts(Integer buyCounts) {
        this.buyCounts = buyCounts;
    }
}
