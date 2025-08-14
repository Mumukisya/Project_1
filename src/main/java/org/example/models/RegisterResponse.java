package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String accessToken;
    private String refreshToken;
    private boolean success;

    private User user;
}
