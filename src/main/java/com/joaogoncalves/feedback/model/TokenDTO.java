package com.joaogoncalves.feedback.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
