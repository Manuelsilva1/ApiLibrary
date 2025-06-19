package com.api.libreria.controller;

import com.api.libreria.model.*;
import com.api.libreria.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final Map<String, Cart> guestCarts = new ConcurrentHashMap<>();
    private long guestItemIdCounter = 1;

    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository,
                          BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // GET carrito actual
    @GetMapping
    public ResponseEntity<Cart> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        if (userDetails != null) {
            Long userId = getUserId(userDetails.getUsername());

            Cart cart = cartRepository.findByUsuarioId(userId).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUsuarioId(userId);
                return cartRepository.save(newCart);
            });

            return ResponseEntity.ok(cart);
        }

        Cart cart = sessionId != null ? guestCarts.get(sessionId) : null;
        if (cart == null) {
            cart = new Cart();
            cart.setItems(new ArrayList<>());
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
            }
            guestCarts.put(sessionId, cart);
        }
        return ResponseEntity.ok()
                .header("X-Session-Id", sessionId)
                .body(cart);
    }

    // POST agregar libro al carrito
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam Long bookId,
                                          @RequestParam Integer cantidad,
                                          @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        if (userDetails != null) {
            Long userId = getUserId(userDetails.getUsername());
            Cart cart = cartRepository.findByUsuarioId(userId).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUsuarioId(userId);
                return cartRepository.save(newCart);
            });

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

        Cart cart = sessionId != null ? guestCarts.get(sessionId) : null;
        if (cart == null) {
            cart = new Cart();
            cart.setItems(new ArrayList<>());
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
            }
            guestCarts.put(sessionId, cart);
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            CartItem item = new CartItem();
            item.setId(guestItemIdCounter++);
            item.setBook(book);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(book.getPrice());
            if (cart.getItems() == null) {
                cart.setItems(new ArrayList<>());
            }
            cart.getItems().add(item);
        }

        guestCarts.put(sessionId, cart);
        return ResponseEntity.ok()
                .header("X-Session-Id", sessionId)
                .body(cart);
    }

    // PUT actualizar cantidad de un item
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateItemQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable Long itemId,
                                                   @RequestParam Integer cantidad,
                                                   @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        if (userDetails != null) {
            Long userId = getUserId(userDetails.getUsername());

            Cart cart = cartRepository.findByUsuarioId(userId)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            CartItem item = cartItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item no encontrado"));

            if (!item.getCart().getId().equals(cart.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (cantidad <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setCantidad(cantidad);
                cartItemRepository.save(item);
            }

            cart = cartRepository.findByUsuarioId(userId).orElse(cart);
            return ResponseEntity.ok(cart);
        }

        Cart cart = sessionId != null ? guestCarts.get(sessionId) : null;
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<CartItem> optItem = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
        if (optItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CartItem item = optItem.get();
        if (cantidad <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setCantidad(cantidad);
        }

        guestCarts.put(sessionId, cart);
        return ResponseEntity.ok()
                .header("X-Session-Id", sessionId)
                .body(cart);
    }

    // DELETE eliminar item del carrito
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long itemId,
                                           @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        if (userDetails != null) {
            Long userId = getUserId(userDetails.getUsername());

            Cart cart = cartRepository.findByUsuarioId(userId)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            CartItem item = cartItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item no encontrado"));

            if (!item.getCart().getId().equals(cart.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            cartItemRepository.delete(item);

            cart = cartRepository.findByUsuarioId(userId).orElse(cart);
            return ResponseEntity.ok(cart);
        }

        Cart cart = sessionId != null ? guestCarts.get(sessionId) : null;
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<CartItem> optItem = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
        if (optItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        cart.getItems().remove(optItem.get());
        guestCarts.put(sessionId, cart);
        return ResponseEntity.ok()
                .header("X-Session-Id", sessionId)
                .body(cart);
    }

    private Long getUserId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }
}
