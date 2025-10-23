package com.zhongxin.sonicshelf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.zhongxin.sonicshelf.mapper")
@EnableTransactionManagement
public class SonicShelfServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonicShelfServerApplication.class, args);
    }

}
