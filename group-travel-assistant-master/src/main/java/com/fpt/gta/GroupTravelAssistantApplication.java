package com.fpt.gta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GroupTravelAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupTravelAssistantApplication.class, args);
    }

}
