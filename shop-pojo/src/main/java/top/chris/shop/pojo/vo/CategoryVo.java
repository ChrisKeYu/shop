package top.chris.shop.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.chris.shop.pojo.Category;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVo {
    /**
     * 主键 分类id主键
     */
    @Id
    @Column(name = "`id`")
    private Integer id;

    /**
     * 分类名称 分类名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 分类类型 分类得类型，
     1:一级大分类
     2:二级分类
     3:三级小分类
     */
    @Column(name = "`type`")
    private Integer type;

    /**
     * 父id 父id 上一级依赖的id，1级分类则为0，二级三级分别依赖上一级
     */
    @Column(name = "`father_id`")
    private Integer fatherId;

    /**
     * 图标 logo
     */
    @Column(name = "`logo`")
    private String logo;

    /**
     * 口号
     */
    @Column(name = "`slogan`")
    private String slogan;

    /**
     * 分类图
     */
    @Column(name = "`cat_image`")
    private String catImage;

    /**
     * 背景颜色
     */
    @Column(name = "`bg_color`")
    private String bgColor;

    /**
     * 用来存储二级菜单中对应的三级菜单。
     */
    private List<Category> subCatList = new ArrayList<Category>();
}
