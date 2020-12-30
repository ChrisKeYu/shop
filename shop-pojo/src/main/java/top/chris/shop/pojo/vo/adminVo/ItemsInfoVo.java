package top.chris.shop.pojo.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ItemsInfoVo {
    private String id;
    private String itemName;
    //得根据item表中得catId去category表中查询对应的分类名称
    private String catName;
    private String content;
    private String sellCounts;
    private Integer onOffStatus;
    private Integer catId;      //三级分类
    private String catIdName;   //三级分类对应的名称
    private Integer secCatgory ; //二级分类
    private String secCatgoryName; //二级分类对应的名称
    private Integer rootCatId;  //一级分类

    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updatedTime;

    //专门用来判断ItemId在ItemSpec、ItemParam、ItemImg表中是否已经存在，存在flag = true，存在flag = false
    private Boolean paramIsExist;
    private Boolean specIsExist;
    private Boolean imgIsExist;
}
