package com.dalmofelipe.SpringJWT.exceptions;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }

    public AlreadyExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
