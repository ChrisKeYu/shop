package top.chris.shop.pojo.bo;

import lombok.Data;
//用于封装传入商品id参数和评论级别参数(后台接受参数名称要和前台传入的名称一致)
@Data
public class CommentBo {
    //商品id
    private String itemId;
    //level参数传入为空时，表示查询所有评论(使用动态sql语句实现)(如果此处的level时Integer类型，那么如果前端传入空值，那么后台接受的时null类型，如果level是int类型，那么后台接收的是0)
    private String level;
}
