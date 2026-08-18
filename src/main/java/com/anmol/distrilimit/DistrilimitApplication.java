package com.anmol.distrilimit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DistrilimitApplication {

	static {
		System.out.println(">>> STATIC BLOCK RUNNING, setting timezone to UTC <<<");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println(">>> JVM timezone is now: " + TimeZone.getDefault().getID() + " <<<");
	}

	public static void main(String[] args) {
		SpringApplication.run(DistrilimitApplication.class, args);
	}
}
