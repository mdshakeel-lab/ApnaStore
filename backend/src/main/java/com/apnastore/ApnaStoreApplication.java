package com.apnastore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApnaStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApnaStoreApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Apna Store Java Spring Boot Backend Started!");
        System.out.println(" REST API URL : http://localhost:8080/api/products");
        System.out.println(" Store Frontend: http://localhost:8080/");
        System.out.println("=================================================");
    }
}
