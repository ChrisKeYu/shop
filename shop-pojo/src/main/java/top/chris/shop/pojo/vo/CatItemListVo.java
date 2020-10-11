package top.chris.shop.pojo.vo;

import io.swagger.models.auth.In;
import lombok.Data;
//使用Lombok可以省略写get/set方法
@Data
public class CatItemListVo {
    private String itemId;
    private String imgUrl;
    private String itemName;
    private Integer price ;
    private Integer sellCounts ;

}
