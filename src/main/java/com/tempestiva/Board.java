package com.tempestiva;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tempestiva.domain.Order;

import java.io.IOException;

public class Board {
    public static void main(String[] args) throws IOException {
        System.out.println("Test");

        ObjectMapper objectMapper = new ObjectMapper();

        Order order = new Order("ID-1", 100);

        System.out.println(objectMapper.writeValueAsString(order));

    }
}
