package org.aionys.notes.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.aionys.notes.persistence.repos.NoteRepository;

@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    @SuppressWarnings("all") // suppress lombok plugin warning
    @Delegate(types = NoteService.class)
    private final NoteRepository noteRepository;

}
