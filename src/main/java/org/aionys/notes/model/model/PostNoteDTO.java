package org.aionys.notes.model.model;

import jakarta.validation.constraints.NotBlank;

public record PostNoteDTO(
        @NotBlank String title,
        @NotBlank String content
) {
}
