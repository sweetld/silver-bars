package com.tempestiva.domain;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Order {
    String id = UUID.randomUUID().toString();
    String userId;
    double quantity;
    long price;
    OrderType orderType;
}
