package org.aionys.main.security.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aionys.main.commons.valiation.groups.Full;
import org.aionys.main.security.jwt.JwtCookieFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
class UserController {

    private final JwtCookieFactory jwtCookieFactory;

    private final UserService userService;

    private final UserMapper userMapper;

    private final Environment environment;

    @PostMapping("/login")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @Operation(summary = "Log in")
    public ResponseEntity<String> login(Principal principal, HttpServletResponse response) {
        var cookies = jwtCookieFactory.forUser(principal.getName());
        for (var c : cookies) {
            response.addCookie(c);
        }

        var responseEntityBuilder = ResponseEntity.ok();

        // for dev profile, return the token in the body so it can be used in swagger or postman easily
        if (environment.matchesProfiles("dev")) {
            return responseEntityBuilder.body(
                    cookies.stream().filter(c -> c.getName().equals("Bearer")).findFirst().orElseThrow().getValue()
            );
        }

        return responseEntityBuilder.build();
    }

    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "403", description = "User already logged in")
    })
    @SecurityRequirements
    @Operation(summary = "Register a new user")
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

    @GetMapping("/me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "User not logged in")
    })
    @SecurityRequirements
    @Operation(summary = "Get the current user")
    public ResponseEntity<GetUserDTO> me(Principal principal) {
        return ResponseEntity.ok(userMapper.toDTO(userService.findByUsername(principal.getName()).orElseThrow(() ->
                // this should never happen
                new IllegalStateException("User with username %s not found".formatted(principal.getName()))
        )));
    }

    @DeleteMapping("/me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted account successfully"),
            @ApiResponse(responseCode = "401", description = "User not logged in")
    })
    @SecurityRequirements
    @Operation(summary = "Delete the current user")
    public ResponseEntity<Void> delete(Principal principal) {
        userService.deleteByUsername(principal.getName());
        return ResponseEntity.noContent().build();
    }

}
