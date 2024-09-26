package com.joaogoncalves.feedback.exception;

public class FeedbackNotFoundException extends RuntimeException {
    public FeedbackNotFoundException(String message){
        super(message);
    }
}
