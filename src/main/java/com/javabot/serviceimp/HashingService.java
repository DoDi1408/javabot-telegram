package com.javabot.serviceimp;

import org.springframework.stereotype.Component;

import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Bcrypt;

@Component
public class HashingService {

    // Generates a hashed string
    public String generateHashFromPassword(String password){
        BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B,12);
        Hash hash = Password.hash(password).addPepper("test-pepper").with(bcrypt);
        return hash.getResult();
    }
    
    public boolean verifyHash(String hashedPassword, String plainTextPassword){
        BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B,12);
        return Password.check(plainTextPassword, hashedPassword).addPepper("test-pepper").with(bcrypt);
    }

}
