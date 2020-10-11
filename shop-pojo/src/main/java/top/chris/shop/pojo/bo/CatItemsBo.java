package top.chris.shop.pojo.bo;

import lombok.Data;


@Data
public class CatItemsBo {
    //分类id
    private Integer catId;
    //1、c表示销量优先(默认销量约高，越前)；2、k表示默认查询；3、p表示价格优先(默认价格约低，越前)
    private String sort;
    //分页页数
    private Integer page;
    //分页每页显示数量
    private Integer pageSize;

}
