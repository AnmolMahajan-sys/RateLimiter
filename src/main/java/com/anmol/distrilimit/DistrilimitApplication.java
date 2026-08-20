package com.anmol.distrilimit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class DistrilimitApplication {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}


	public static void main(String[] args) {
		SpringApplication.run(DistrilimitApplication.class, args);
	}
}
