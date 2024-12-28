package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.services.MyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final Logger logger = LoggerFactory.getLogger(MyController.class);

    @Autowired
    private MyService service;

    @GetMapping("/division")
    public ResponseEntity<Double> division(@RequestParam Double numerator, @RequestParam Double denominator) {
        return ResponseEntity.ok(service.division(numerator,denominator));
    }

    @GetMapping("/")
    public String boasVindas() {
        logger.atInfo().setMessage("User accessed main page.").log();
        return "Bem vindo à aplicação.";
    }


}
