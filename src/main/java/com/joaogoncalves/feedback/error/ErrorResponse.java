package com.joaogoncalves.feedback.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;

    public ErrorResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
