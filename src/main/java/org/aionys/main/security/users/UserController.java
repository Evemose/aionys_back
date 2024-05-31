package org.aionys.main.security.users;

import lombok.RequiredArgsConstructor;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.security.jwt.JwtEncryptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
class UserController {

    private final JwtEncryptor jwtEncryptor;

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<String> login(Principal principal) {
        return ResponseEntity.ok(jwtEncryptor.encrypt(principal.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<GetUserDTO> register(
            @RequestBody @Validated(Full.class) PostUserDTO dto,
            Principal principal) {
        // for some, reason, not().authenticated()
        // does not permit access for non-authenticated users
        // so we have to check it manually
        if (principal != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userMapper.toDTO(userService.save(userMapper.toEntity(dto))));
    }
}
