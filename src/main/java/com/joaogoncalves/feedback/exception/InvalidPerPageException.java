package com.joaogoncalves.feedback.exception;

public class InvalidPerPageException extends RuntimeException {
    public InvalidPerPageException(String message){
        super(message);
    }
}
