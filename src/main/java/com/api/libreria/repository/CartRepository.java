package com.api.libreria.repository;

import com.api.libreria.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUsuarioId(Long usuarioId);
}
