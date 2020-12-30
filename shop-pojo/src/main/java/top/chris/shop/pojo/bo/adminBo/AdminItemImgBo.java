package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

@Data
public class AdminItemImgBo {
    private String itemId;
    //顺序 图片顺序，从小到大
    private Integer sort;
    //是否主图 是否主图，1：是，0：否
    private Integer isMain;
}
