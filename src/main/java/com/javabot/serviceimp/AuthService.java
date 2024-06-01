package com.javabot.serviceimp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.javabot.models.Employee;

@Component
public class AuthService {

    private final String authURL = "http://auth-app-service:8080";
    private final static Logger loggerAuthService = LoggerFactory.getLogger(AuthService.class);

    public String createJWTfromEmployee(Employee employee){
        loggerAuthService.info("Making a create request to AuthService");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Employee> request = new HttpEntity<Employee>(employee);
        return restTemplate.postForEntity(authURL + "/auth/create", request, String.class).getBody();
    }

    public ResponseEntity<?> getEmployeeFromJWT(String jwt){
        loggerAuthService.info("Making a verify request to AuthService");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(jwt);
        try {
            ResponseEntity<Employee> response = restTemplate.postForEntity(authURL + "/auth/verify", request, Employee.class);
            return response;
        }
        catch (HttpClientErrorException rce){
            loggerAuthService.error("4xx Error Code",rce);
            return ResponseEntity.status(rce.getStatusCode()).body(rce.getResponseBodyAsString());
        }
        catch (HttpServerErrorException seex){
            loggerAuthService.error("5xx Error Code",seex);
            return ResponseEntity.status(seex.getStatusCode()).body(seex.getResponseBodyAsString());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
