package com.javabot.telegram;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
	
	@Autowired
  	private UserRepository userRepository;

    @Test
	void contextLoads() {
		try {
			userRepository.testConnection();
        } catch (Exception e) {
            System.out.println("EXCEPTION WHILE TESTING DATABASE CONNECTION: " + e.getMessage());
            throw new RuntimeException("I throw this error IF database connection fails so that the build fails, sorry, check database config", e);
        }
	}
	
}
