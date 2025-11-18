package com.group5final.roomieradar;

import com.group5final.roomieradar.services.user.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoomieRadarApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RoomieRadarApplication.class, args);
        var userService = context.getBean(UserService.class);
    }

}
