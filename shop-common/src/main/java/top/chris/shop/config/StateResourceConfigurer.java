package top.chris.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


@Configuration
public class StateResourceConfigurer extends WebMvcConfigurationSupport {
    /**
     * 配置访问静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/shop_Image/**").addResourceLocations("file:D:/shop_Image/");
        super.addResourceHandlers(registry);
    }


}
