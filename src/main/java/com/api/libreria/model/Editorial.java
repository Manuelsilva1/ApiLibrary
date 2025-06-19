package com.api.libreria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "editoriales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String sitioWeb;
}
