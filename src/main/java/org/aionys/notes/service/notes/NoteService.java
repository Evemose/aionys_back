package org.aionys.notes.service.notes;

import org.aionys.notes.persistence.model.entity.Note;

import java.util.List;
import java.util.Optional;

public interface NoteService {
    Note save(Note note);
    List<Note> findAllByOwner_Username(String username);
    Optional<Note> findByIdAndOwner_Username(Long id, String username);
    void deleteByIdAndOwner_Username(Long id, String username);
}
