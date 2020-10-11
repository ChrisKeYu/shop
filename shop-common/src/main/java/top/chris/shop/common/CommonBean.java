package top.chris.shop.common;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBean {
    /**
     * 向容器中注入分页对象。
     * @return
     */
    @Bean
    public PageHelper pageHelper(){
        return new PageHelper();
    }
}
