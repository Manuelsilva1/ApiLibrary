package com.api.libreria.repository;

import com.api.libreria.model.VentaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaItemRepository extends JpaRepository<VentaItem, Long> {
}
