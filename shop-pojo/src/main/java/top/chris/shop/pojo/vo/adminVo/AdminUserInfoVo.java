package top.chris.shop.pojo.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AdminUserInfoVo {
    private String  id ;
    private String nickname ;
    private String realname ;
    private Integer sex ;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date birthday ;
    private String email ;
    private String mobile;
}
