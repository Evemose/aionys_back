package org.aionys.main.security.users;

import java.time.LocalDateTime;

record GetUserDTO(
        String username,
        String password,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
