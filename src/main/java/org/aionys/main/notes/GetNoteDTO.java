package org.aionys.main.notes;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "GetNoteDTO",
        description = "DTO returned by the GET /notes/** endpoint",
        example = """
                {
                    "id": 1,
                    "title": "Note title",
                    "content": "Note content",
                    "createdAt": "2021-10-10T10:10:10",
                    "lastModifiedAt": "2021-10-10T10:10:10",
                    "profilePicture": "base64-encoded-image"
                """)
record GetNoteDTO(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
