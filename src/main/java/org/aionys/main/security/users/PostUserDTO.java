package org.aionys.main.security.users;

import jakarta.validation.constraints.NotBlank;
import org.aionys.main.commons.valiation.annotations.NullOrNotBlank;
import org.aionys.main.commons.valiation.annotations.Password;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.commons.valiation.groups.Partial;

record PostUserDTO(
        @NotBlank(groups = Full.class)
        @NullOrNotBlank(groups = Partial.class)
        String username,

        @NotBlank(groups = Full.class)
        @NullOrNotBlank(groups = Partial.class)
        @Password
        String password
) {
}
