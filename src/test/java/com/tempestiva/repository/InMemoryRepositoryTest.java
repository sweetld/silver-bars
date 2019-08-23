package com.tempestiva.repository;

import com.tempestiva.domain.Order;
import com.tempestiva.domain.OrderType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class InMemoryRepositoryTest {
    private OrderRepository orderRepository;

    @Before
    public void intialise() {
        orderRepository = new InMemoryRepository();
    }

    @Test
    public final void whenIntialisedThenEmpty() {
        final TreeMap<Long, Double> sellPricesByQuantity = new TreeMap<>();
        final TreeMap<Long, Double> buyPricesByQuantity = new TreeMap<>(Comparator.reverseOrder());

        Assert.assertEquals(buyPricesByQuantity, orderRepository.getBuyPricesByQuantity());
        Assert.assertEquals(sellPricesByQuantity, orderRepository.getSellPricesByQuantity());
        Assert.assertEquals(Collections.emptyList(), orderRepository.getBuyOrders());
        Assert.assertEquals(Collections.emptyList(), orderRepository.getSellOrders());
    }

    @Test
    public final void whenRegisterOrdersThenCorrectListPopulated() {
        Order sellOrder1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        Order sellOrder2 = Order.builder().userId("user3").quantity(2.5).price(306).orderType(OrderType.SELL).build();

        Order buyOrder1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.BUY).build();
        Order buyOrder2 = Order.builder().userId("user3").quantity(2.5).price(306).orderType(OrderType.BUY).build();

        orderRepository.registerOrder(buyOrder2);
        orderRepository.registerOrder(sellOrder1);
        orderRepository.registerOrder(buyOrder1);
        orderRepository.registerOrder(sellOrder2);

        List<Order> sellOrders = new ArrayList<>();
        sellOrders.add(sellOrder1);
        sellOrders.add(sellOrder2);

        List<Order> buyOrders = new ArrayList<>();
        buyOrders.add(buyOrder2);
        buyOrders.add(buyOrder1);

        Assert.assertEquals(sellOrders, orderRepository.getSellOrders());
        Assert.assertEquals(buyOrders, orderRepository.getBuyOrders());
    }

    @Test
    public final void whenCancelOrderThenListEmpty() {
        Order sellOrder1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        orderRepository.registerOrder(sellOrder1);
        orderRepository.cancelOrder(sellOrder1);

        Assert.assertEquals(Collections.emptyList(), orderRepository.getSellOrders());
        Assert.assertEquals(Collections.emptyList(), orderRepository.getBuyOrders());

        final TreeMap<Long, Double> buyPricesByQuantity = new TreeMap<>(Comparator.reverseOrder());

        Assert.assertEquals(buyPricesByQuantity, orderRepository.getBuyPricesByQuantity());
        Assert.assertEquals(0,
                            orderRepository.getSellPricesByQuantity()
                                           .entrySet()
                                           .stream()
                                           .filter(entry -> entry.getValue() != 0)
                                           .count());
    }

    @Test
    public final void whenCancelNonExistingOrderThenListStillContainsSameContent() {
        Order sellOrder1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        Order sellOrder2 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        orderRepository.registerOrder(sellOrder1);
        orderRepository.registerOrder(sellOrder1);
        orderRepository.cancelOrder(sellOrder2);

        List<Order> sellOrders = new ArrayList<>();
        sellOrders.add(sellOrder1);
        sellOrders.add(sellOrder1);

        Assert.assertEquals(sellOrders, orderRepository.getSellOrders());
        Assert.assertEquals(Collections.emptyList(), orderRepository.getBuyOrders());

        final TreeMap<Long, Double> buyPricesByQuantity = new TreeMap<>(Comparator.reverseOrder());
        final TreeMap<Long, Double> sellPricesByQuantity = new TreeMap<>();
        sellPricesByQuantity.put(306L, 2d);

        Assert.assertEquals(buyPricesByQuantity, orderRepository.getBuyPricesByQuantity());
        Assert.assertEquals(sellPricesByQuantity, orderRepository.getSellPricesByQuantity());
    }

}
