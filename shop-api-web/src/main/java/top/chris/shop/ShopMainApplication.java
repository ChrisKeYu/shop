package top.chris.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@MapperScan(basePackages = "top.chris.shop.mapper") //Mapper接口扫描器(Mapper接口可以在不同的模块下，只要包名确定了就可以扫描到)，于对应的mapper.xml组合在一起使用
@ComponentScan(basePackages = {"org.n3r.idworker","top.chris.shop"})
@EnableSwagger2
@EnableScheduling //开启定时任务
@EnableCaching //开启缓存
@EnableAsync //开启异步
public class ShopMainApplication {
    /**
     * SpringBoot工程如果出现了bug，而且没有在控制台输出任何日志，那么就在启动类上加上try-catch。
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            SpringApplication.run(ShopMainApplication.class,args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Bean
    MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation("D:/shop_Image");
        return factory.createMultipartConfig();
    }
}
