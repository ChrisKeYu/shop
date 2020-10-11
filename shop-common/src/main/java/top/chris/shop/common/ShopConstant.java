package top.chris.shop.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties //支持使用yml配置文件
@ConfigurationProperties("top.chris.shop.constant") //读取yml配置文件中前缀为指定字符串的数据，只要yml中数据改变了，该配置类中的对应的数据就会被取代
public class ShopConstant {
    /**
     * 注册用户的默认头像
     */
    public static final String defaultUserFaceImage = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";
}
