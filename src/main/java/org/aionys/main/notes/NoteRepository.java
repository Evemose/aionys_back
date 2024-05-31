package org.aionys.main.notes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOwner_Username(String username);

    Optional<Note> findByIdAndOwner_Username(Long id, String username);

    boolean existsByIdAndOwner_Username(Long id, String username);
}
