package com.tempestiva.domain;

import lombok.Data;

@Data
public class Order {
    private final String id;
    private final long price;
}
