package com.api.libreria.controller;

import com.api.libreria.model.Oferta;
import com.api.libreria.service.OfertaService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public List<Oferta> getAll() {
        return ofertaService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Oferta> getById(@PathVariable Long id) {
        return ofertaService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Oferta> create(@RequestBody Oferta oferta) {
        Oferta saved = ofertaService.save(oferta);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Oferta> update(@PathVariable Long id, @RequestBody Oferta oferta) {
        Oferta existing = ofertaService.getById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        existing.setNombre(oferta.getNombre());
        existing.setDescripcion(oferta.getDescripcion());
        existing.setDescuento(oferta.getDescuento());

        Oferta updated = ofertaService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ofertaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ofertaId}/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Oferta> addBook(@PathVariable Long ofertaId, @PathVariable Long bookId) {
        Oferta updated = ofertaService.addBook(ofertaId, bookId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ofertaId}/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Oferta> removeBook(@PathVariable Long ofertaId, @PathVariable Long bookId) {
        Oferta updated = ofertaService.removeBook(ofertaId, bookId);
        return ResponseEntity.ok(updated);
    }
}
