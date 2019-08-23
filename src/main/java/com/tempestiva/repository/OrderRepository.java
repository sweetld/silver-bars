package com.tempestiva.repository;

import com.tempestiva.domain.Order;

import java.util.List;
import java.util.Map;

public interface OrderRepository {

    List<Order> getSellOrders();

    List<Order> getBuyOrders();

    Map<Long, Double> getSellPricesByQuantity();

    Map<Long, Double> getBuyPricesByQuantity();

    void registerOrder(Order order);

    void cancelOrder(Order order);

}
