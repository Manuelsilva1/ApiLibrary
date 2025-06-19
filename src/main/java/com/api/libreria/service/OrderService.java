package com.api.libreria.service;

import com.api.libreria.model.*;
import com.api.libreria.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {
        Cart cart = cartRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Order order = new Order();
        order.setUser(userRepository.findById(userId).orElseThrow());
        order.setDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotal(0.0);

        order = orderRepository.save(order);

        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(item.getBook());
            orderItem.setQuantity(item.getCantidad());
            orderItem.setPrice(item.getPrecioUnitario());
            orderItemRepository.save(orderItem);

            total += item.getCantidad() * item.getPrecioUnitario();
        }

        order.setTotal(total);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getItems());

        return order;
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
