package top.chris.shop.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersVo {
    /**
     * 主键id 用户id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 用户名 用户名
     */
    @Column(name = "`username`")
    private String username;

    /**
     * 头像 头像
     */
    @Column(name = "`face`")
    private String face;

    /**
     * 性别 性别 1:男  0:女  2:保密
     */
    @Column(name = "`sex`")
    private Integer sex;
}
