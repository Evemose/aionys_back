package org.aionys.notes.service;

import org.aionys.notes.persistence.model.entity.Note;

import java.util.Optional;

public interface NoteService {
    Note save(Note note);
    Optional<Note> findById(Long id);
    void deleteById(Long id);
}
