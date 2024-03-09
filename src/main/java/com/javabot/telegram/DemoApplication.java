package com.javabot.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		// Get all environment variables
		Map<String, String> envMap = System.getenv();

		// Get specific variables
		String databaseUrl = envMap.get("DATABASE_URL");
		String directory = envMap.get("DIRECTORY");
	
		// Print the values
		System.out.println("DATABASE_URL: " + databaseUrl);
		System.out.println("DIRECTORY: " + directory);
	}

}
