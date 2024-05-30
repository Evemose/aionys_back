package org.aionys.main.security.users;

import lombok.RequiredArgsConstructor;
import org.aionys.main.security.jwt.JwtEncryptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
class UserController {

    private final JwtEncryptor jwtEncryptor;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(Principal principal) {
        return ResponseEntity.ok(jwtEncryptor.encrypt(principal.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PostUserDTO dto) {
       throw new UnsupportedOperationException("Not implemented");
    }
}
