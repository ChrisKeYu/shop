package top.chris.shop.pojo.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdminItemSpecInfoVo {
    private String id ;
    private String itemId ;
    private String itemName;
    private String name;
    private Integer stock;
    private BigDecimal discounts ;
    private Integer priceDiscount;
    private Integer priceNormal;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updatedTime;

}
