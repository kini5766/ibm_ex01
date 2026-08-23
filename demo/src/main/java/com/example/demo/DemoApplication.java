package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

// https://github.com/bezkoder/spring-security-refresh-token-jwt
// https://github.com/topics/refresh-token-rotation
// https://github.com/parsin/JWT-Authentication-with-Refresh-Token-Rotation-and-Redis-Storage/
// https://thachtaro2210.github.io/posts/springboot-jwt-refresh-rotation/
@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
