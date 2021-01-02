package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

@Data
public class AdminSearchItemParamBo {
    //用户名称
    private String userName;
    //三级分类
    private Integer catIdCatgory0;
    //一级分类
    private Integer category0;
    //商品名称
    private String itemName;
    //主题
    private String isMain;

}
