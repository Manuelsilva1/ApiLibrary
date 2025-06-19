package com.api.libreria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "venta_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer cantidad;
    private Double precioUnitario;
}
