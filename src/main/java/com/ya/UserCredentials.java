package com.ya;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCredentials {
    private String email;
    private String password;
    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }
}