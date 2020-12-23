package top.chris.shop.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoVo {
 private String nickname;
 private String realname;
 private Integer sex;
 private String mobile;
 private String email;
 @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
 private Date birthday;
 //浏览器访问服务器中对应图片的地址
 private String faceUrl;
}
