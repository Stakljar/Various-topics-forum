package com.example.various_topics_forum.exception.exceptions;

public class UserAlreadyEnabledException extends RuntimeException {
    private static final long serialVersionUID = -7075423876844058985L;

    public UserAlreadyEnabledException(String message) {
        super(message);
    }
}
