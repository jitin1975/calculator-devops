package edu.iiitb.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();

    @Test
    void addsTwoNumbers() {
        assertEquals(5.0, service.add(2, 3));
    }

    @Test
    void subtractsTwoNumbers() {
        assertEquals(-1.0, service.subtract(2, 3));
    }

    @Test
    void multipliesTwoNumbers() {
        assertEquals(6.0, service.multiply(2, 3));
    }

    @Test
    void dividesTwoNumbers() {
        assertEquals(2.0, service.divide(6, 3));
    }

    @Test
    void divideByZeroThrows() {
        assertThrows(DivideByZeroException.class, () -> service.divide(6, 0));
    }
}
