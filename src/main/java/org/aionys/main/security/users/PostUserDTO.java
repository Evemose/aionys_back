package org.aionys.main.security.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.aionys.main.commons.valiation.annotations.NullOrNotBlank;
import org.aionys.main.commons.valiation.annotations.Password;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.commons.valiation.groups.Partial;


@Schema(
        name = "PostUserDTO",
        description = "DTO for creating a new user account or updating an existing one"
)
record PostUserDTO(
        @NotBlank(groups = Full.class)
        @NullOrNotBlank(groups = Partial.class)
        @Schema(description = "Unique username", example = "uniqueUsername")
        String username,

        @Schema(description = "Password must contain at least 8 characters, " +
                "one uppercase letter, one lowercase letter, one digit and one special character",
                example = "pass123G$%!")
        @Password(groups = {Full.class, Partial.class})
        String password,

        @Schema(description = "Base64 encoded profile picture", example = "data:image/png;base64,...")
        @NullOrNotBlank(groups = Partial.class)
        String profilePicture
) {
    PostUserDTO(String username, String password) {
        this(username, password, null);
    }
}
