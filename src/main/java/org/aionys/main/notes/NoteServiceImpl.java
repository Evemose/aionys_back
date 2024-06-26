package org.aionys.main.notes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class NoteServiceImpl implements NoteService {

    @SuppressWarnings("all") // suppress lombok plugin warning
    @Delegate(types = NoteService.class)
    private final NoteRepository noteRepository;

    @Override
    public Note save(Note note) {
        return noteRepository.saveAndFlush(note);  // save so entity is audited
    }

    @Override
    public void deleteByIdAndOwner_Username(Long id, String username) {
        if (noteRepository.existsByIdAndOwner_Username(id, username)) {
            noteRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Note with id %d not found".formatted(id));
        }
    }

}
