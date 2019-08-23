package com.tempestiva.domain;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Order {
    private final String id = UUID.randomUUID().toString();
    private final String userId;
    private final double quantity;
    private final long price;
    private final OrderType orderType;
}
