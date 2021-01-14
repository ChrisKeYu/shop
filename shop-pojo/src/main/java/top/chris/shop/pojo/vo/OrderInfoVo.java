package top.chris.shop.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderInfoVo {
    //订单编号
    private String orderId;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    //创建订单的时间
    private Date createdTime;
    //List集合存储订单里面的具体商品信息
    private List<OrderItemInfoVo> subOrderItemList;
    //支付方式
    private Integer payMethod;
    //实际支付金额
    private Integer realPayAmount;
    //邮费
    private Integer postAmount;
    //订单状态
    private String orderStatus;
    //是否评论
    private Integer isComment;
    //是否已经删除
    private Integer isDelete;

}
