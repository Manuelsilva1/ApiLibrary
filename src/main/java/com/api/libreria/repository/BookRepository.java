package com.api.libreria.repository;

import com.api.libreria.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Métodos: findAll(), findById(), save(), deleteById(), etc.
}
