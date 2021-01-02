package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

@Data
public class AdminUpdateImageBo {
    String itemId;
    //需要被修改的状态码
    String isMain;
    //原来的状态码
    String oledStatus;
    String imageId;
}
