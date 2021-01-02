package top.chris.shop.pojo.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class AdminItemParamVo {

    private String id;
    private String itemId;
    private String itemName;
    private String producPlace;
    private String factoryName;
    private String factoryAddress;
    private String brand;
    private String packagingMethod;
    private String eatMethod;
    private String footPeriod;
    private String weight;
    private String storageMethod;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime ;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updatedTime;

}
