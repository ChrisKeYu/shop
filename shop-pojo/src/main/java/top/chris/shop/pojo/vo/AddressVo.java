package top.chris.shop.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressVo {
    private String id;
    private String receiver;
    private String mobile;
    private String province;
    private String district;
    private String detail;
    private String city;
    private Integer isDefault;

}
