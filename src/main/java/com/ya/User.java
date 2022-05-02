package com.ya;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}