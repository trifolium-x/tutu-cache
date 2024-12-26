package io.github.tri5m.tucache.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @title: ExampleApplication
 * @author: trifolium.wang
 * @date: 2022/7/1
 * @modified :
 */
@SpringBootApplication(scanBasePackages = "io.github.tri5m.tucache.example", proxyBeanMethods = false)
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }

}
