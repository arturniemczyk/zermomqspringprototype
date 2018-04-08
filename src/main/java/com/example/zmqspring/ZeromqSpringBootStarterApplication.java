package com.example.zmqspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ZeromqSpringBootStarterApplication extends SpringApplicationBuilder {

	public static void main(String[] args) {
		SpringApplication.run(ZeromqSpringBootStarterApplication.class, args);
	}
}
