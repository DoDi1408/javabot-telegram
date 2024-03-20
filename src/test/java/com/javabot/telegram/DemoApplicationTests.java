package com.javabot.telegram;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
	
	@Test
    void testGetEnvironmentVariable() {
		// Get all environment variables
		Map<String, String> envMap = System.getenv();
		// Get specific variables
		String directory = envMap.get("DIRECTORY");
		String dbuser = envMap.get("DATABASE_USER");
		String dbpwd = envMap.get("DATABASE_PWD");

        // Print the values
		System.out.println("DIRECTORY: " + directory);
		System.out.println("dbuser: " + dbuser);
		System.out.println("dbpwd: " + dbpwd);

    }

    @Test
	void contextLoads() {
	}

}
