package com.tempestiva.service;

import com.tempestiva.domain.Order;
import com.tempestiva.domain.OrderType;
import com.tempestiva.domain.SummaryOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class OrderBoardServiceTest {
    private OrderBoardService orderBoardService;

    @Before
    public void intialise() {
        orderBoardService = new OrderBoardService();
    }

    @Test
    public final void whenIntialisedThenEmpty() {
        Assert.assertEquals(Collections.emptyList(), orderBoardService.summary());
    }

    @Test
    public final void whenRegisteredSellOrderThenSingleSellSummary() {
        Order order1 = Order.builder().userId("user1").quantity(3.5).price(306).orderType(OrderType.SELL).build();
        orderBoardService.registerOrder(order1);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(SummaryOrder.builder().price(306).orderType(OrderType.SELL).quantity(3.5).build());

        Assert.assertEquals(expected, orderBoardService.summary());
    }

    @Test
    public final void whenRegisterOrderAndCancelOrderThenEmpty() {
        Order order1 = Order.builder().userId("user1").quantity(3.5).price(306).orderType(OrderType.SELL).build();
        orderBoardService.registerOrder(order1);
        orderBoardService.cancelOrder(order1);

        Assert.assertEquals(Collections.emptyList(), orderBoardService.summary());
    }

    @Test
    public final void whenOrdersWithSamePriceAndDirectionThenAggregate() {
        Order order1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        Order order2 = Order.builder().userId("user3").quantity(2.5).price(306).orderType(OrderType.SELL).build();
        orderBoardService.registerOrder(order1);
        orderBoardService.registerOrder(order2);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(aggregateOrder(order1, order2));

        Assert.assertEquals(expected, orderBoardService.summary());
    }

    @Test
    public final void whenCancelOrderNotPresentThenNoEffect() {
        Order order1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        Order order2 = Order.builder().userId("user3").quantity(2.5).price(306).orderType(OrderType.BUY).build();
        orderBoardService.registerOrder(order1);
        orderBoardService.cancelOrder(order2);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(convertTo(order1));

        Assert.assertEquals(expected, orderBoardService.summary());
    }

    @Test
    public final void whenMixOfSellAndBuyOrdersThenSummarySortedCorrectly() {
        Order sellOrder1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        Order sellOrder2 = Order.builder().userId("user1").quantity(1).price(307).orderType(OrderType.SELL).build();
        Order sellOrder3 = Order.builder().userId("user1").quantity(1).price(308).orderType(OrderType.SELL).build();
        Order buyOrder1 = Order.builder().userId("user3").quantity(2.5).price(310).orderType(OrderType.BUY).build();
        Order buyOrder2 = Order.builder().userId("user3").quantity(2.5).price(309).orderType(OrderType.BUY).build();
        Order buyOrder3 = Order.builder().userId("user3").quantity(2.5).price(308).orderType(OrderType.BUY).build();

        orderBoardService.registerOrder(sellOrder1);
        orderBoardService.registerOrder(buyOrder1);
        orderBoardService.registerOrder(buyOrder2);
        orderBoardService.registerOrder(sellOrder3);
        orderBoardService.registerOrder(buyOrder3);
        orderBoardService.registerOrder(sellOrder2);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(convertTo(sellOrder1));
        expected.add(convertTo(sellOrder2));
        expected.add(convertTo(sellOrder3));
        expected.add(convertTo(buyOrder1));
        expected.add(convertTo(buyOrder2));
        expected.add(convertTo(buyOrder3));

        Assert.assertEquals(expected, orderBoardService.summary());
    }

    @Test
    public final void whenOrdersWithSamePriceOnlyThenDoNotAggregate() {
        Order order1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.BUY).build();
        Order order2 = Order.builder().userId("user3").quantity(2.5).price(306).orderType(OrderType.SELL).build();
        orderBoardService.registerOrder(order1);
        orderBoardService.registerOrder(order2);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(convertTo(order2));
        expected.add(convertTo(order1));

        Assert.assertEquals(expected, orderBoardService.summary());
    }

    @Test
    public final void whenMultipleBuyAndSellOrdersThenCorrectSummary() {
        Order order1 = Order.builder().userId("user1").quantity(3.5).price(306).orderType(OrderType.SELL).build();
        Order order2 = Order.builder().userId("user2").quantity(1.2).price(310).orderType(OrderType.SELL).build();
        Order order3 = Order.builder().userId("user3").quantity(1.5).price(307).orderType(OrderType.SELL).build();
        Order order4 = Order.builder().userId("user4").quantity(2).price(306).orderType(OrderType.SELL).build();
        Order order5 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.SELL).build();
        Order order6 = Order.builder().userId("user2").quantity(1.7).price(316).orderType(OrderType.BUY).build();
        Order order7 = Order.builder().userId("user4").quantity(2.5).price(310).orderType(OrderType.BUY).build();
        Order order8 = Order.builder().userId("user1").quantity(1).price(316).orderType(OrderType.BUY).build();
        Order order9 = Order.builder().userId("user3").quantity(2).price(309).orderType(OrderType.BUY).build();

        orderBoardService.registerOrder(order1);
        orderBoardService.registerOrder(order2);
        orderBoardService.registerOrder(order3);
        orderBoardService.registerOrder(order4);
        orderBoardService.registerOrder(order5);
        orderBoardService.registerOrder(order6);
        orderBoardService.registerOrder(order7);
        orderBoardService.registerOrder(order8);
        orderBoardService.registerOrder(order9);
        orderBoardService.registerOrder(order9);
        orderBoardService.registerOrder(order9);

        orderBoardService.cancelOrder(order5);
        orderBoardService.cancelOrder(order9);
        orderBoardService.cancelOrder(order9);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(aggregateOrder(order1, order4));
        expected.add(convertTo(order3));
        expected.add(convertTo(order2));
        expected.add(aggregateOrder(order6, order8));
        expected.add(convertTo(order7));
        expected.add(convertTo(order9));

        final List<SummaryOrder> summary = orderBoardService.summary();

        Assert.assertEquals(expected.size(), summary.size());
        Assert.assertEquals(expected, summary);
    }

    @Test
    public final void whenConcurrentOrdersThenNoOrdersLost() {
        Order order1 = Order.builder().userId("user1").quantity(1).price(306).orderType(OrderType.BUY).build();
        Order order2 = Order.builder().userId("user3").quantity(2.5).price(306).orderType(OrderType.SELL).build();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

        executor.execute(() -> orderBoardService.registerOrder(order1));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));

        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.cancelOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.cancelOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.cancelOrder(order2));

        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));
        executor.execute(() -> orderBoardService.registerOrder(order2));

        executor.shutdown();

        orderBoardService.registerOrder(order2);

        List<SummaryOrder> expected = new ArrayList<>();
        expected.add(SummaryOrder.builder()
                                 .orderType(order2.getOrderType())
                                 .quantity(order2.getQuantity() * 10)
                                 .price(order2.getPrice())
                                 .build());
        expected.add(convertTo(order1));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(expected, orderBoardService.summary());
    }

    private SummaryOrder convertTo(Order order) {
        return SummaryOrder.builder()
                           .orderType(order.getOrderType())
                           .quantity(order.getQuantity())
                           .price(order.getPrice())
                           .build();
    }

    private SummaryOrder aggregateOrder(Order order1, Order order2) {
        return SummaryOrder.builder()
                           .orderType(order1.getOrderType())
                           .quantity(order1.getQuantity() + order2.getQuantity())
                           .price(order1.getPrice())
                           .build();
    }
}
