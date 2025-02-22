package com.example.Exception;

public class DuplicatedEmail extends RuntimeException {
    public DuplicatedEmail() {
        super();
    }

    public DuplicatedEmail(String message) {
        super(message);
    }

    public DuplicatedEmail(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedEmail(Throwable cause) {
        super(cause);
    }
}
