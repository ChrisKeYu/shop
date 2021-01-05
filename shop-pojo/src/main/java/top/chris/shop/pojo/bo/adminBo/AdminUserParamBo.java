package top.chris.shop.pojo.bo.adminBo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminUserParamBo {
    private String  id;
    private String userName;
    private String   birthday;
    private String  nickname;
    private String  realname;
    private Integer  sex;
    private String  email;
    private String  mobile;


}
