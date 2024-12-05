package com.infy.transaction_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomerException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return new ResponseEntity<>("Customer not found error occurred: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MonthlyDiscountPointsArithmeticException.class)
    public ResponseEntity<String> handleMonthlyDiscountPointsArithmeticException(MonthlyDiscountPointsArithmeticException ex) {
        return new ResponseEntity<>("Monthly discount points arithmetic error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MonthlyDiscountPointsException.class)
    public ResponseEntity<String> handleMonthlyDiscountPointsException(MonthlyDiscountPointsException ex) {
        return new ResponseEntity<>("Monthly discount points error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CalculateDiscountPointsException.class)
    public ResponseEntity<String> handleCalculateDiscountPointsException(CalculateDiscountPointsException ex) {
        return new ResponseEntity<>("Calculate discount points error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
