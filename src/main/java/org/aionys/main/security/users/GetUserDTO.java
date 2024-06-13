package org.aionys.main.security.users;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "GetUserDTO",
        description = "DTO for getting user information",
        example = """
                {
                    "username": "uniqueUsername",
                    "profilePicture": "http://localhost:8080/images/1",
                    "createdAt": "2021-08-01T12:00:00",
                    "lastModifiedAt": "2021-08-01T12:00:00"
                }
                """)
record GetUserDTO(
        String username,
        String profilePicture,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
