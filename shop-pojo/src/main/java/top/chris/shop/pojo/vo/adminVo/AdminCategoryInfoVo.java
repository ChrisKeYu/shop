package top.chris.shop.pojo.vo.adminVo;

import lombok.Data;

@Data
public class AdminCategoryInfoVo {
    private Integer id;
    private String name;
    private Integer type;
    private Integer fatherId;
    private String fatherName;
    private String logo;
    private String slogan;
    private String catImage;
    private String bgColor;
}
