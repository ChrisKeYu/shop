package top.chris.shop.pojo.vo.adminVo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminCarouselInfoVo {
    private String id;
    private String imageUrl;
    private String backgroundColor;
    private String itemId;
    private String itemName;
    private String catId; //三级
    private String catName;
    private Integer type;
    private Integer sort;
    private Integer isShow;
    private Date createTime;
    private Date updateTime;


    private String category;//一级
    private String secCatgory;//二级
    private String secCatgoryName; //二级名称
    private String catIdName;//三级名称
}
