package com.tempestiva.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SummaryOrder {
    double quantity;
    long price;
    OrderType orderType;
}
