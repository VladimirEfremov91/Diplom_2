package com.ya;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTokens {
    private String refreshToken;
    private String accessToken;
    public UserTokens(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}