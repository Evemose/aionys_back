package org.aionys.main.security.users;

import java.time.LocalDateTime;

record GetUserDTO(
        String username,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
