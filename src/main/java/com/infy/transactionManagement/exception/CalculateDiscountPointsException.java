package com.infy.transactionManagement.exception;

public class CalculateDiscountPointsException extends RuntimeException {
    private String message;
    public CalculateDiscountPointsException(){}
    public CalculateDiscountPointsException(String message) {
        super(message);
        this.message = message;
    }
}
