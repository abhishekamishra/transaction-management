package com.infy.transaction_management.exception;

public class MonthlyDiscountPointsException extends RuntimeException {
    private String message;
    public MonthlyDiscountPointsException(){}
    public MonthlyDiscountPointsException(String message) {
        super(message);
        this.message = message;
    }
}
