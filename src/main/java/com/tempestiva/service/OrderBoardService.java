package com.tempestiva.service;

import com.tempestiva.domain.SummaryOrder;
import com.tempestiva.domain.Order;
import com.tempestiva.domain.OrderType;
import com.tempestiva.repository.InMemoryRepository;
import com.tempestiva.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderBoardService {
    private OrderRepository orderRepository = new InMemoryRepository();

    public void registerOrder(Order order) {
        orderRepository.registerOrder(order);
    }

    public void cancelOrder(Order order) {
        orderRepository.cancelOrder(order);
    }

    public List<SummaryOrder> summary() {
        final Stream<SummaryOrder> buyStream = orderRepository.getBuyPricesByQuantity().entrySet()
                                                              .stream()
                                                              .filter(entry -> entry.getValue() != 0)
                                                              .map(entry -> SummaryOrder.builder()
                                                                                        .price(entry.getKey())
                                                                                        .quantity(entry.getValue())
                                                                                        .orderType(OrderType.BUY)
                                                                                        .build());

        final Stream<SummaryOrder> sellStream = orderRepository.getSellPricesByQuantity().entrySet()
                                                               .stream()
                                                               .filter(entry -> entry.getValue() != 0)
                                                               .map(entry -> SummaryOrder.builder()
                                                                                         .price(entry.getKey())
                                                                                         .quantity(entry.getValue())
                                                                                         .orderType(OrderType.SELL)
                                                                                         .build());

        return Stream.concat(sellStream, buyStream).collect(Collectors.toList());
    }

}
