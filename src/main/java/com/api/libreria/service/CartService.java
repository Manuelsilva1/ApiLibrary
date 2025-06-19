package com.api.libreria.service;

import com.api.libreria.model.*;
import com.api.libreria.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUsuarioId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUsuarioId(userId);
                    return cartRepository.save(newCart);
                });
    }

    public Cart addItemToCart(Long userId, Book book, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingItem = cart.getItems() == null ? Optional.empty() :
            cart.getItems().stream().filter(item -> item.getBook().getId().equals(book.getId())).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCantidad(item.getCantidad() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setBook(book);
            item.setCantidad(quantity);
            item.setPrecioUnitario(book.getPrice());
            cartItemRepository.save(item);
        }

        return cartRepository.findByUsuarioId(userId).orElse(cart);
    }
}
