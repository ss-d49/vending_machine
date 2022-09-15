package com.daysoft.vending_machine;

public class insufficientChangeError extends Exception {
    public insufficientChangeError(String message) {
        super(message);
    }
}
