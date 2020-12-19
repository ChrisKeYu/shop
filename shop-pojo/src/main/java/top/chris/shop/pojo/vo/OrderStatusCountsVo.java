package top.chris.shop.pojo.vo;

import lombok.Data;

@Data
public class OrderStatusCountsVo {
    //待付款
    private Integer waitPayCounts;
    //待发货
    private Integer waitDeliverCounts;
    //待收货
    private Integer waitReceiveCounts;
    //待评价
    private Integer waitCommentCounts;
}
