package org.aionys.main.security.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aionys.main.exceptionhandling.FieldError;
import org.aionys.main.security.jwt.JwtDecryptor;
import org.aionys.main.utils.RequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.aionys.main.utils.RequestBuilder.Credentials;
import static org.aionys.main.utils.RequestBuilder.Credentials.VALID_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginLogoutIntegrationTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtDecryptor jwtDecryptor;

    private static Stream<Arguments> invalidCredentialsAndExplanation() {
        return Stream.of(
                Arguments.of(new Credentials(VALID_CREDENTIALS.username(), "wrong"), "Invalid password"),
                Arguments.of(new Credentials("wrong", "admin"), "Invalid username")
        );
    }

    public static Stream<Arguments> invalidRegisterData() {
        final var invalidPasswordMessage = "Password must contain at least 8 characters, " +
                "one uppercase letter, one lowercase letter, one digit and one special character";
        return Stream.of(
                Arguments.of(new Credentials("new", "short"),
                        List.of(new FieldError(
                                invalidPasswordMessage,
                                "password",
                                "short"
                        )), HttpStatus.BAD_REQUEST
                ),
                Arguments.of(new Credentials("new", "longpaFG2d"),
                        List.of(new FieldError(
                                invalidPasswordMessage,
                                "password",
                                "longpaFG2d"
                        )), HttpStatus.BAD_REQUEST
                ),
                Arguments.of(new Credentials(" ", VALID_CREDENTIALS.password()),
                        List.of(new FieldError(
                                "must not be blank",
                                "username",
                                " "
                        )), HttpStatus.BAD_REQUEST
                ),
                Arguments.of(VALID_CREDENTIALS,
                        List.of(), // unfortunately, h2 returns error in other format than postgres,
                        // so exception handler will not provide any additional information
                        HttpStatus.CONFLICT
                ),
                Arguments.of(new Credentials(" ", "password"),
                        List.of(new FieldError(
                                        "must not be blank",
                                        "username",
                                        " "
                                ),
                                new FieldError(
                                        "Password must contain at least 8 characters, " +
                                                "one uppercase letter, one lowercase letter, " +
                                                "one digit and one special character",
                                        "password",
                                        "password"
                                )
                        ), HttpStatus.BAD_REQUEST
                )
        );
    }

    @Test
    public void testLogin_WhenUserExists_ThenReturnToken() throws Exception {
        var receivedToken = new RequestBuilder(mockMvc)
                .authorization(RequestBuilder.Authorization.BASIC)
                .credentials(VALID_CREDENTIALS)
                .perform(post("/login"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(VALID_CREDENTIALS.username(), jwtDecryptor.extractUsername(receivedToken));
    }

    @ParameterizedTest
    @MethodSource("invalidCredentialsAndExplanation")
    public void testLogin_WhenUserDoesNotExist_ThenReturnUnauthorized(
            Credentials credentials, String explanation
    ) throws Exception {
        new RequestBuilder(mockMvc)
                .authorization(RequestBuilder.Authorization.BASIC)
                .credentials(credentials)
                .perform(post("/login"))
                .andExpect(result -> assertEquals(401, result.getResponse().getStatus(), explanation));
    }

    @Test
    @DirtiesContext
    public void testRegister_WhenUserDoesNotExist_ThenOk() throws Exception {
        new RequestBuilder(mockMvc)
                .body(new PostUserDTO("new", VALID_CREDENTIALS.password()))
                .perform(post("/register"))
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    public void testRegister_WhenLoggedIn_ThenForbidden() throws Exception {
        new RequestBuilder(mockMvc)
                .authorization(RequestBuilder.Authorization.BASIC)
                .credentials(VALID_CREDENTIALS)
                .body(new Credentials("new", VALID_CREDENTIALS.password()))
                .perform(post("/register"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("invalidRegisterData")
    @DirtiesContext
    public void testRegister_WhenInvalidData_ThenBadRequest(
            Credentials credentials,
            List<FieldError> explanation,
            HttpStatus status
    ) throws Exception {
        var response = new RequestBuilder(mockMvc)
                .body(new PostUserDTO(credentials.username(), credentials.password()))
                .perform(post("/register"))
                .andExpect(status().is(status.value()))
                .andReturn();

        if (explanation.isEmpty()) {
            return;
        }

        var receivedErrors = objectMapper.readValue(
                response.getResponse().getContentAsString(), FieldError[].class
        );

        assertThat(receivedErrors).containsExactlyInAnyOrderElementsOf(explanation);
    }
}
