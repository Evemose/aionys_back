package org.aionys.main.notes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.commons.valiation.groups.Partial;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
class NoteController {

    private final NoteService noteService;

    private final NoteMapper noteMapper;

    @GetMapping
    public ResponseEntity<List<GetNoteDTO>> getAll(Principal principal) {
        return ResponseEntity.ok(
                noteService.findAllByOwner_Username(principal.getName()).stream()
                        .map(noteMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetNoteDTO> get(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(
                noteService.findByIdAndOwner_Username(id, principal.getName())
                        .map(noteMapper::toDTO)
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GetNoteDTO> update(
            @PathVariable Long id,
            @RequestBody @Validated(Partial.class) PostNoteDTO noteDTO,
            Principal principal) {
        var entity = noteService.findByIdAndOwner_Username(id, principal.getName())
                .orElseThrow(EntityNotFoundException::new);
        noteMapper.mapNonNullIntoEntity(noteDTO, entity);
        return ResponseEntity.ok(noteMapper.toDTO(noteService.save(entity)));
    }

    @PostMapping
    public ResponseEntity<GetNoteDTO> create(@RequestBody @Validated(Full.class) PostNoteDTO noteDTO) {
        var entity = noteMapper.toEntity(noteDTO);
        return ResponseEntity.ok(noteMapper.toDTO(noteService.save(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        noteService.deleteByIdAndOwner_Username(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
