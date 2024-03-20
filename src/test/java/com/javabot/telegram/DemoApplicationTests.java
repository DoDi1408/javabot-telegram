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
		String dbuser2 = envMap.get("DB_CREDENTIALS_USR");
		String dbpwd2 = envMap.get("DB_CREDENTIALS_PSW");
        // Print the values
		System.out.println("DIRECTORY: " + directory);
		System.out.println("dbuser: " + dbuser);
		System.out.println("dbpwd: " + dbpwd);
		System.out.println("dbuser: " + dbuser2);
		System.out.println("dbpwd: " + dbpwd2);

    }

    @Test
	void contextLoads() {
	}

}
