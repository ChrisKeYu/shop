package top.chris.shop.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoBo {
    private String nickname;
    private String realname;
    private Integer sex;
    private String mobile;
    private String email;
    private Date birthday;

}

