package com.api.libreria.controller;

import com.api.libreria.model.Editorial;
import com.api.libreria.repository.EditorialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/editoriales")
public class EditorialController {

    private final EditorialRepository editorialRepository;

    public EditorialController(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    @GetMapping
    public Page<Editorial> getAll(Pageable pageable) {
        return editorialRepository.findAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Editorial> create(@RequestBody Editorial editorial) {
        Editorial saved = editorialRepository.save(editorial);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Editorial> update(@PathVariable Long id, @RequestBody Editorial editorial) {
        Editorial existing = editorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editorial no encontrada"));

        existing.setNombre(editorial.getNombre());
        existing.setSitioWeb(editorial.getSitioWeb());

        Editorial updated = editorialRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        editorialRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
