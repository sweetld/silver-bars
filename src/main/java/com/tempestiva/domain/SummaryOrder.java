package com.tempestiva.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SummaryOrder {
    private final double quantity;
    private final long price;
    private final OrderType orderType;
}
