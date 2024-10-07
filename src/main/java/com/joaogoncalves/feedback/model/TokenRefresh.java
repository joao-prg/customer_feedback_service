package com.joaogoncalves.feedback.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefresh {

    @NotNull
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotNull
    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;

    @NotNull
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
}
