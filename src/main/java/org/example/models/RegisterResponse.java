package org.example.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterResponse {
    private String accessToken;
    private String refreshToken;
    private boolean success;

    private User user;
}
