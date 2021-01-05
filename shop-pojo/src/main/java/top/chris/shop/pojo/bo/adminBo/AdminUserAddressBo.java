package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

@Data
public class AdminUserAddressBo {

    private String id;
    private String userId;
    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
}
