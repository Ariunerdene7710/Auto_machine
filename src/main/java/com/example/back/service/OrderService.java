package com.example.back.service;

import com.example.back.entity.*;
import com.example.back.repository.OrderRepository;
import com.example.back.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CarRepository carRepository;

    @Transactional
    public Order createOrder(User user, List<OrderItem> items, String shippingAddress, String contactPhone) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setShippingAddress(shippingAddress);
        order.setContactPhone(contactPhone);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Car machine = carRepository.findById(item.getMachine().getId())
                    .orElseThrow(() -> new RuntimeException("Machine not found with id: " + item.getMachine().getId()));

            Integer stockQuantity = machine.getStockQuantity() != null ? machine.getStockQuantity() : machine.getStock();
            if (stockQuantity == null || stockQuantity < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for machine: " + machine.getName());
            }

            item.setOrder(order);
            item.setMachine(machine);
            item.setUnitPrice(machine.getPrice());
            BigDecimal itemTotal = machine.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setTotalPrice(itemTotal);

            totalAmount = totalAmount.add(itemTotal);

            // Update stock
            machine.setStockQuantity(stockQuantity - item.getQuantity());
            machine.setStock(stockQuantity - item.getQuantity());
            carRepository.save(machine);
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}