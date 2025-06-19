package com.api.libreria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LibroRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    private String isbn;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "Debe especificar la editorial")
    private Long editorialId;

    @NotNull(message = "Debe especificar la categoría")
    private Long categoriaId;

    private String descripcion;

    private String coverImage;
}
