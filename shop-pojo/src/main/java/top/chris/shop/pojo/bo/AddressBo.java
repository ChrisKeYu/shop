package top.chris.shop.pojo.bo;

import lombok.Data;

import java.util.Date;

@Data
public class AddressBo {
    //用户地址表的主键id
    private String addressId;
    //用户id
    private String userId;
    //城市
    private String city;
    //详细地址
    private String detail;
    //区县
    private String district;
    //电话
    private String mobile;
    //省份
    private String province;
    //收件人姓名
    private String receiver;
    //修改时间
    private Date updatedTime;
    //默认地址
    private Integer isDefault;
}
