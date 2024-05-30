package org.aionys.notes.persistence.repos;

import org.aionys.notes.persistence.model.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>{
}
