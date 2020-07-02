package com.chenxin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther chenxin4
 * @Date 2020/7/2
 * Description
 */
@SpringBootApplication(scanBasePackages = "com.chenxin")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
