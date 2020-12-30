package top.chris.shop.pojo.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class AllUserInfoVo {
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
     * 昵称 昵称
     */
    @Column(name = "`nickname`")
    private String nickname;

    /**
     * 真实姓名 真实姓名
     */
    @Column(name = "`realname`")
    private String realname;

    /**
     * 头像 头像
     */
    @Column(name = "`face`")
    private String face;

    /**
     * 手机号 手机号
     */
    @Column(name = "`mobile`")
    private String mobile;

    /**
     * 邮箱地址 邮箱地址
     */
    @Column(name = "`email`")
    private String email;

    /**
     * 性别 性别 1:男  0:女  2:保密
     */
    @Column(name = "`sex`")
    private Integer sex;

    /**
     * 生日 生日
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Column(name = "`birthday`")
    private Date birthday;

    /**
     * 创建时间 创建时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Column(name = "`created_time`")
    private Date createdTime;

    /**
     * 更新时间 更新时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Column(name = "`updated_time`")
    private Date updatedTime;

}
