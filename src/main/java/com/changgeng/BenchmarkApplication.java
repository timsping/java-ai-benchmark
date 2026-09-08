package com.changgeng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

//@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.changgeng.mapper")
@EnableFeignClients
public class BenchmarkApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(BenchmarkApplication.class, args);
	}

}
