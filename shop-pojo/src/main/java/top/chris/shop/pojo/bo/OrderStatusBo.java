package top.chris.shop.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusBo {
    private String orderId;
    private Integer orderStatus;
    private Date payTime;
    private Date deliverTime;
    private Date successTime;
    private Date closeTime;
    private Date commentTime;
}
