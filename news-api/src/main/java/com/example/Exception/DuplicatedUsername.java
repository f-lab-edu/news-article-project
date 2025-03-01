package com.example.Exception;

public class DuplicatedUsername extends RuntimeException {
    public DuplicatedUsername() {
        super();
    }

    public DuplicatedUsername(String message) {
        super(message);
    }

    public DuplicatedUsername(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedUsername(Throwable cause) {
        super(cause);
    }
}
