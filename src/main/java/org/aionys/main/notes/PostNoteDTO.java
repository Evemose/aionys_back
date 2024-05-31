package org.aionys.main.notes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.aionys.main.commons.valiation.annotations.NullOrNotBlank;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.commons.valiation.groups.Partial;

@Schema(name = "PostNoteDTO",
        description = "DTO for creating a new note or updating an existing one.",
        example = """
                {
                    "title": "Note title",
                    "content": "Note content"
                }
                """
)
record PostNoteDTO(
        @NotBlank(groups = Full.class) @NullOrNotBlank(groups = Partial.class) String title,
        @NotBlank(groups = Full.class) @NullOrNotBlank(groups = Partial.class) String content
) {
}
