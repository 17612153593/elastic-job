package com.tansun.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tansun.batch.mapper")
public class BatchApplication {

    public static void main(String[] args) {

        SpringApplication.run(BatchApplication.class, args);
    }

}
