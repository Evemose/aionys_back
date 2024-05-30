package org.aionys.notes.model.model;

public record GetNoteDTO(
        Long id,
        String title,
        String content,
        String createdAt,
        String lastModifiedAt
) {
}
