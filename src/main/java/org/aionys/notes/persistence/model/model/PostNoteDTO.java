package org.aionys.notes.persistence.model.model;

import jakarta.validation.constraints.NotBlank;

public record PostNoteDTO(
        @NotBlank String title,
        @NotBlank String content
) {
}
