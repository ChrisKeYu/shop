package top.chris.shop.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data //lombok插件，帮助成员属性覆盖get/set方法
@Configuration
@EnableConfigurationProperties //支持使用yml配置文件
@ConfigurationProperties("top.chris.shop") //读取yml配置文件中前缀为指定字符串的数据，只要yml中数据改变了，该配置类中的对应的数据就会被取代
public class ShopProperties {
    //静态前端资源路径
    private String basicStaticServerUrl = "http://localhost:8848";
    //前端Cookie保存的购物车名称
    private String shopCarCookieName = "shopcart";


}
