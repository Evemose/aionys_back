package org.aionys.notes.dto.notes;

import jakarta.validation.constraints.NotBlank;
import org.aionys.notes.valiation.annotations.NullOrNotBlank;
import org.aionys.notes.valiation.groups.Full;
import org.aionys.notes.valiation.groups.Partial;

public record PostNoteDTO(
        @NotBlank(groups = Full.class) @NullOrNotBlank(groups = Partial.class) String title,
        @NotBlank(groups = Full.class) @NullOrNotBlank(groups = Partial.class) String content
) {
}
