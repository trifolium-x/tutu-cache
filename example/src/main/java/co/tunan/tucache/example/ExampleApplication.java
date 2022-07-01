package co.tunan.tucache.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @title: ExampleApplication
 * @author: trifolium.wang
 * @date: 2022/7/1
 * @modified :
 */
@SpringBootApplication(scanBasePackages = "co.tunan.tucache.example", proxyBeanMethods = false)
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }

}
