package com.api.libreria.dto;

import lombok.Data;

@Data
public class LibroResponse {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Double precio;
    private Integer stock;
    private Long editorialId;
    private Long categoriaId;
    private String descripcion;
    private String coverImage;
    private String dateAdded;
}
