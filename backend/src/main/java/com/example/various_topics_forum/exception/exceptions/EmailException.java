package com.example.various_topics_forum.exception.exceptions;

public class EmailException extends RuntimeException {
    private static final long serialVersionUID = -1466085023472054467L;

    public EmailException(String message) {
        super(message);
    }
}
