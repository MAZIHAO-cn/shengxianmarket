package com.mazihao.shengxianmarket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = "com.mazihao.shengxianmarket.model.dao")
@EnableSwagger2
@EnableCaching
public class ShengxianmarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShengxianmarketApplication.class, args);
    }

}
