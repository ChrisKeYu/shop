package top.chris.shop.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import top.chris.shop.common.ShopConstant;
import top.chris.shop.common.ShopProperties;

/**
 * 解决跨域访问路径安全问题的配置
 */
@Configuration
public class CorsConfig {

    @Autowired
    private ShopProperties shopProperties;
    /**
     * 创建一个已经配置好的CorsFilter过滤器对象。注入到框架中，使得框架能够对前端请求的url地址进行识别，拦截自定义路径的路径或放行自定义的路径。
     * @return
     */
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //添加允许访问本服务的源路径(域名+端口)，类似添加一个放行组
        corsConfiguration.addAllowedOrigin(shopProperties.getBasicStaticServerUrl());
        //允许认证
        corsConfiguration.setAllowCredentials(true);
        //允许访问的方法,*表示所有的方法
        corsConfiguration.addAllowedMethod("*");
        //允许访问的请求头,*表示任何的请求头
        corsConfiguration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //允许访问的请求路径
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}
