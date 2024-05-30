package org.aionys.main.notes;

import jakarta.validation.constraints.NotBlank;
import org.aionys.main.commons.valiation.annotations.NullOrNotBlank;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.commons.valiation.groups.Partial;

record PostNoteDTO(
        @NotBlank(groups = Full.class) @NullOrNotBlank(groups = Partial.class) String title,
        @NotBlank(groups = Full.class) @NullOrNotBlank(groups = Partial.class) String content
) {
}
