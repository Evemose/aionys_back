package org.aionys.notes.dto.notes;

public record GetNoteDTO(
        Long id,
        String title,
        String content,
        String createdAt,
        String lastModifiedAt
) {
}
