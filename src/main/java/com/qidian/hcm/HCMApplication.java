package com.qidian.hcm;

import lombok.extern.slf4j.Slf4j;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HCMApplication {
    public static void main(String[] args) {
        SpringApplication.run(HCMApplication.class, args);
    }
}
