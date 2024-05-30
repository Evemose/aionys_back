package org.aionys.notes.service.notes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.aionys.notes.persistence.repos.NoteRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoteServiceImpl implements NoteService {

    @SuppressWarnings("all") // suppress lombok plugin warning
    @Delegate(types = NoteService.class)
    private final NoteRepository noteRepository;

    @Override
    public void deleteByIdAndOwner_Username(Long id, String username) {
        if (noteRepository.existsByIdAndOwner_Username(id, username)) {
            noteRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException();
        }
    }

}