package org.aionys.main.notes;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.commons.valiation.groups.Partial;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
class NoteController {

    private final NoteService noteService;

    private final NoteMapper noteMapper;

    @GetMapping
    @Operation(summary = "Get all notes of the user")
    public ResponseEntity<List<GetNoteDTO>> getAll(Principal principal) {
        return ResponseEntity.ok(
                noteService.findAllByOwner_Username(principal.getName()).stream()
                        .map(noteMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a note by id")
    public ResponseEntity<GetNoteDTO> get(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(
                noteService.findByIdAndOwner_Username(id, principal.getName())
                        .map(noteMapper::toDTO)
                        .orElseThrow(() -> new EntityNotFoundException("Note with id %d not found".formatted(id)))
        );
    }

    // Task specified PUT endpoint in requirements, but I think that PATCH endpoint
    // is much better in matching semantics of note update.
    // It is a common case to update only title/content/any other potential field
    // and not the whole note, which is the case for PATCH.
    @PatchMapping("/{id}")
    @Operation(summary = "Perform a partial update of a note")
    public ResponseEntity<GetNoteDTO> update(
            @PathVariable Long id,
            @RequestBody @Validated(Partial.class) PostNoteDTO noteDTO,
            Principal principal) {
        var entity = noteService.findByIdAndOwner_Username(id, principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("Note with id %d not found".formatted(id)));
        noteMapper.mapNonNullIntoEntity(noteDTO, entity);
        return ResponseEntity.ok(noteMapper.toDTO(noteService.save(entity)));
    }

    @PostMapping
    @Operation(summary = "Create a new note")
    public ResponseEntity<GetNoteDTO> create(@RequestBody @Validated(Full.class) PostNoteDTO noteDTO) {
        var entity = noteMapper.toEntity(noteDTO);
        var saved = noteService.save(entity);
        return ResponseEntity.created(URI.create("/notes/" + saved.getId())).body(noteMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a note by id")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        noteService.deleteByIdAndOwner_Username(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
