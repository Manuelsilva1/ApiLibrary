package com.api.libreria.service;

import com.api.libreria.model.Book;
import com.api.libreria.model.Oferta;
import com.api.libreria.repository.BookRepository;
import com.api.libreria.repository.OfertaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final BookRepository bookRepository;

    public OfertaService(OfertaRepository ofertaRepository, BookRepository bookRepository) {
        this.ofertaRepository = ofertaRepository;
        this.bookRepository = bookRepository;
    }

    public List<Oferta> getAll() {
        return ofertaRepository.findAll();
    }

    public Optional<Oferta> getById(Long id) {
        return ofertaRepository.findById(id);
    }

    public Oferta save(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }

    public void delete(Long id) {
        ofertaRepository.deleteById(id);
    }

    public Oferta addBook(Long ofertaId, Long bookId) {
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        if (oferta.getBooks() == null) {
            oferta.setBooks(new ArrayList<>());
        }

        if (!oferta.getBooks().contains(book)) {
            oferta.getBooks().add(book);
        }
        return ofertaRepository.save(oferta);
    }

    public Oferta removeBook(Long ofertaId, Long bookId) {
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        if (oferta.getBooks() != null) {
            oferta.getBooks().removeIf(b -> b.getId().equals(bookId));
        }
        return ofertaRepository.save(oferta);
    }
}
