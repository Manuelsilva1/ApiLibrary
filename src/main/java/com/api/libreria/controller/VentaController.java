package com.api.libreria.service;

import com.api.libreria.model.Venta;
import com.api.libreria.model.*;
import com.api.libreria.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VentaServic {

    private final VentaRepository ventaRepository;
    private final VentaItemRepository ventaItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public VentaServic(VentaRepository ventaRepository, VentaItemRepository ventaItemRepository,
                        CartRepository cartRepository, CartItemRepository cartItemRepository,
                        BookRepository bookRepository, UserRepository userRepository) {
        this.ventaRepository = ventaRepository;
        this.ventaItemRepository = ventaItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Venta crearVentaDesdeCarrito(Long userId, String metodoPago) {
        Cart cart = cartRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Validar stock
        for (CartItem item : cart.getItems()) {
            if (item.getBook().getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + item.getBook().getTitle());
            }
        }

        // Descontar stock
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            book.setStock(book.getStock() - item.getCantidad());
            bookRepository.save(book);
        }

        Venta venta = new Venta();
        venta.setUsuarioId(userId);
        venta.setFecha(LocalDateTime.now());
        venta.setMetodoPago(metodoPago);
        venta.setTotal(0.0);

        venta = ventaRepository.save(venta);

        double total = 0.0;

        for (CartItem item : cart.getItems()) {
            VentaItem ventaItem = new VentaItem();
            ventaItem.setVenta(venta);
            ventaItem.setBook(item.getBook());
            ventaItem.setCantidad(item.getCantidad());
            ventaItem.setPrecioUnitario(item.getPrecioUnitario());
            ventaItemRepository.save(ventaItem);

            total += item.getCantidad() * item.getPrecioUnitario();
        }

        venta.setTotal(total);
        ventaRepository.save(venta);

        // Vaciar carrito
        cartItemRepository.deleteAll(cart.getItems());

        return venta;
    }
}
