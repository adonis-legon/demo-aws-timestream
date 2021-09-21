package com.example.demoawstimestream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class DemoAwsTimestreamApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(DemoAwsTimestreamApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Application metrics is started collecting...");
	}

}
