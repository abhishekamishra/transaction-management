package com.infy.transaction_management.exception;

public class MonthlyDiscountPointsArithmeticException extends RuntimeException {
    private String message;
    public MonthlyDiscountPointsArithmeticException(){}
    public MonthlyDiscountPointsArithmeticException(String message) {
        super(message);
        this.message = message;
    }
}
