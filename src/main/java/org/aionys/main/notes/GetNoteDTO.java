package org.aionys.main.notes;

record GetNoteDTO(
        Long id,
        String title,
        String content,
        String createdAt,
        String lastModifiedAt
) {
}
