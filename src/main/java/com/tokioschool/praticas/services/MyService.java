package com.tokioschool.praticas.services;

import org.springframework.stereotype.Service;

@Service
public class MyService {

    public double division(double numerator, double denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Division by 0 (zero) is not allowed.");
        }
        return numerator / denominator;
    }
}
