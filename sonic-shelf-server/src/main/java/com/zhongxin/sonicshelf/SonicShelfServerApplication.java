package com.zhongxin.sonicshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@MapperScan("com.sonicshelf.mapper")
@EnableTransactionManagement
public class SonicShelfServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonicShelfServerApplication.class, args);
    }

}
