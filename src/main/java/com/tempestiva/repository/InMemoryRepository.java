package com.tempestiva.repository;

import com.tempestiva.domain.Order;
import com.tempestiva.domain.OrderType;

import java.util.*;

public class InMemoryRepository implements OrderRepository {
    private final Object sellLock = new Object();
    private final Object buyLock = new Object();
    private final List<Order> sellOrders = new ArrayList<>();
    private final List<Order> buyOrders = new ArrayList<>();
    private final TreeMap<Long, Double> sellPricesByQuantity = new TreeMap<>();
    private final TreeMap<Long, Double> buyPricesByQuantity = new TreeMap<>(Comparator.reverseOrder());

    @Override
    public List<Order> getSellOrders() {
        return Collections.unmodifiableList(sellOrders);
    }

    @Override
    public List<Order> getBuyOrders() {
        return Collections.unmodifiableList(buyOrders);
    }

    @Override
    public Map<Long, Double> getSellPricesByQuantity() {
        return Collections.unmodifiableNavigableMap(sellPricesByQuantity);
    }

    @Override
    public Map<Long, Double> getBuyPricesByQuantity() {
        return Collections.unmodifiableNavigableMap(buyPricesByQuantity);
    }

    @Override
    public void registerOrder(final Order order) {
        if (order.getOrderType() == OrderType.SELL) {
            synchronized (sellLock) {
                sellOrders.add(order);
                sellPricesByQuantity.compute(order.getPrice(),
                                             (k, v) -> v == null ? order.getQuantity() : v + order.getQuantity());
            }
        } else {
            synchronized (buyLock) {
                buyOrders.add(order);
                buyPricesByQuantity.compute(order.getPrice(),
                                            (k, v) -> v == null ? order.getQuantity() : v + order.getQuantity());
            }
        }
    }

    @Override
    public void cancelOrder(final Order order) {
        if (order.getOrderType() == OrderType.SELL) {
            synchronized (sellLock) {
                if (sellOrders.remove(order)) {
                    sellPricesByQuantity.computeIfPresent(order.getPrice(), (k, v) -> v - order.getQuantity());
                }
            }
        } else {
            synchronized (buyLock) {
                if (buyOrders.remove(order)) {
                    buyPricesByQuantity.computeIfPresent(order.getPrice(), (k, v) -> v - order.getQuantity());
                }
            }
        }
    }
}
