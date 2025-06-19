package com.api.libreria.controller;

import com.api.libreria.model.Book;
import com.api.libreria.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book request) {
        Book saved = bookRepository.save(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable Long id,
                                           @Valid @RequestBody Book request) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        existing.setTitle(request.getTitle());
        existing.setAuthor(request.getAuthor());
        existing.setEditorial(request.getEditorial());
        existing.setCategory(request.getCategory());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setDescription(request.getDescription());
        existing.setImageUrl(request.getImageUrl());

        Book updated = bookRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
