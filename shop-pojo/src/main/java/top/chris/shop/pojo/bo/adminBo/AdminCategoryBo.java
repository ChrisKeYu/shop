package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

@Data
public class AdminCategoryBo {
    private String id;
    private String name;
    private String type;//根据用户选择不同的类别（可自由选择1~3级）
    private String category;//一级分类(直接输入名字，不需要给他添加编号，数据库会自增，只需要往后台传入名字和它的顶层级分类【0】，当一级分类超过10个后就不允许添加)
    private String secCatgory;//二级分类(直接输入名字，不需要给他添加编号，数据库会自增，只需要往后台传入名字和它的一级分类)
    private String catIdCatgory;//三级分类(直接输入名字，不需要给他添加编号，数据库会自增，只需要往后台传入名字和它的二级分类)
    private String fatherId;//上级Id
    private String slogan;//口号：只有type为1的才有权力设置Log
    private String bgColor;//背景色，只有type为1的才有权力设置背景色
    //logo;//分类图标：后台默认给出
}
