package com.api.libreria.controller;

import com.api.libreria.model.*;
import com.api.libreria.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
// Allow access to regular users and admins
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository,
                          BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // GET carrito actual
    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails.getUsername());

        Cart cart = cartRepository.findByUsuarioId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUsuarioId(userId);
            return cartRepository.save(newCart);
        });

        return ResponseEntity.ok(cart);
    }

    // POST agregar libro al carrito
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam Long bookId,
                                          @RequestParam Integer cantidad) {

        Long userId = getUserId(userDetails.getUsername());
        Cart cart = cartRepository.findByUsuarioId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUsuarioId(userId);
            return cartRepository.save(newCart);
        });

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        // Buscamos si el item ya existe en el carrito
        Optional<CartItem> existingItem = cart.getItems() == null ? Optional.empty() :
            cart.getItems().stream().filter(item -> item.getBook().getId().equals(bookId)).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCantidad(item.getCantidad() + cantidad);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setBook(book);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(book.getPrice());
            item.setCart(cart);
            cartItemRepository.save(item);
        }

        cart = cartRepository.findByUsuarioId(userId).get();
        return ResponseEntity.ok(cart);
    }

    private Long getUserId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }
}
