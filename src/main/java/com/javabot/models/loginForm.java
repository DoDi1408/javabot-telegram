package com.javabot.models;

public class loginForm {
    private String telegramId;
    private String password;
    private String email;
    
    
    public loginForm(String telegramId, String password, String email) {
        this.telegramId = telegramId;
        this.password = password;
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public String getTelegramId() {
        return telegramId;
    }
    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }
}
