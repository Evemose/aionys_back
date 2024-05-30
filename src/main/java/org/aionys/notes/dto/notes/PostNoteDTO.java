package org.aionys.notes.dto.notes;

import jakarta.validation.constraints.NotBlank;

public record PostNoteDTO(
        @NotBlank String title,
        @NotBlank String content
) {
}
