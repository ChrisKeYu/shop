package top.chris.shop.pojo.bo;

import org.hibernate.validator.constraints.Length;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UsersBo {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @Length(min = 6,message = "密码长度必须大于6位")
    private String password;
    private String confirmPassword;
}
