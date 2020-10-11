package top.chris.shop.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Data
public class RenderSixItemsVo {
    /**
     * 分类名称 分类名称
     */
    @Column(name = "`name`")
    private String rootCatName;
    /**
     * 口号
     */
    @Column(name = "`slogan`")
    private String slogan;
    /**
     * 背景颜色
     */
    @Column(name = "`bg_color`")
    private String bgColor;
    /**
     * 分类图
     */
    @Column(name = "`cat_image`")
    private String catImage;

    private List<SimpleItem> simpleItemList = new ArrayList<>();

}
