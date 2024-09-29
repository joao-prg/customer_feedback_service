package com.joaogoncalves.feedback.exception;

public class UserNotAuthorOfFeedbackException extends RuntimeException {
    public UserNotAuthorOfFeedbackException(String message){
        super(message);
    }
}
