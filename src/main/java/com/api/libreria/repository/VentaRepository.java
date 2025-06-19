package com.api.libreria.repository;

import com.api.libreria.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    Page<Venta> findByUsuarioId(Long usuarioId, Pageable pageable);
}
