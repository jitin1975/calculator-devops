package edu.iiitb.calculator;

public class DivideByZeroException extends RuntimeException {

    public DivideByZeroException() {
        super("division by zero is not allowed");
    }
}
