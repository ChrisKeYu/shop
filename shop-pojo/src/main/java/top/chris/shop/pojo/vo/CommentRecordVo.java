package top.chris.shop.pojo.vo;

import lombok.Data;

import java.util.Date;
@Data
public class CommentRecordVo {
    //评论者头像
    private String userFace;
    //评论者昵称
    private String nickname;
    //评论时间
    private Date createdTime;
    //评价内容
    private String content;
    //商品规格
    private String specName;




}
