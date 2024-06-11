package org.aionys.main.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
public final class RequestBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;
    private Object body;
    private String credentialsAsBase;

    private CompletableFuture<Cookie[]> authCookies;

    private Authorization authorization = Authorization.BEARER;

    private boolean authorize = false;

    public RequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private void clearBearer() {
        if (authCookies != null) {
            authCookies.cancel(true);
            authCookies = null;
        }
    }

    /**
     * Sets the credentials for the request. If the credentials are already set, they will be overridden.
     * If the authorization type is set to bearer, the request will be sent
     * to the /login endpoint to retrieve the bearer token.
     *
     * @param credentials the credentials
     * @return this builder
     */
    public RequestBuilder credentials(@NonNull Credentials credentials) {
        this.authorize = true;
        if (authCookies != null) {
            log.warn("Credentials have been already set. They will be overridden.");
            clearBearer();
        }
        this.credentialsAsBase = "Basic " + Base64.getEncoder().encodeToString((
                credentials.username + ":" + credentials.password
        ).getBytes());
        if (authorization == Authorization.BEARER) {
            retrieveBearer();
        }
        return this;
    }

    /**
     * Sets the authorization type. If the authorization type is already set to bearer, it will be overridden.
     *
     * @param authorization the authorization type
     * @return this builder
     */
    public RequestBuilder authorization(@NonNull Authorization authorization) {
        if (authCookies != null && authorization != Authorization.BEARER) {
            log.warn("Authorization type has already been set to bearer. It will be overridden.");
            clearBearer();
        }
        if (authorization == Authorization.BEARER) {
            retrieveBearer();
        }
        this.authorization = authorization;
        return this;
    }

    private void retrieveBearer() {
        authCookies = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return mockMvc.perform(post("/login")
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                credentialsAsBase
                                        )).andReturn().getResponse().getHeaders(HttpHeaders.SET_COOKIE)
                                .stream()
                                .flatMap(header -> Arrays.stream(header.split(";")))
                                .filter(e -> e.matches("Bearer[a-zA-Z]+=.*"))
                                .map(e -> e.split("="))
                                .map(e -> new Cookie(e[0], e[1]))
                                .toArray(Cookie[]::new);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    /**
     * Sets the body for the request. If the body is already set, it will be overridden.
     *
     * @param body the body
     * @return this builder
     */
    public RequestBuilder body(Object body) {
        if (this.body != null) {
            log.warn("Body has been already set. It will be overridden.");
        }
        this.body = body;
        return this;
    }

    /**
     * Performs the request.
     *
     * @param requestBuilder the request builder
     * @return the result actions
     * @throws Exception if an during the request occurs
     */
    public ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        if (authorize) {
            appendAuth(requestBuilder);
        }
        if (body != null) {
            requestBuilder.contentType("application/json");
            requestBuilder.content(objectMapper.writeValueAsString(body));
        }
        return mockMvc.perform(requestBuilder);
    }

    private void appendAuth(MockHttpServletRequestBuilder requestBuilder) throws InterruptedException, ExecutionException {
        if (authorization == Authorization.BEARER) {
            requestBuilder.cookie(authCookies.get());
        } else {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, credentialsAsBase);
        }
    }

    public enum Authorization {
        BEARER, BASIC
    }

    public record Credentials(String username, String password) {
        public static final Credentials VALID_CREDENTIALS = new Credentials("user1", "123Ffg%1!");

        public Credentials {
            if (username == null || password == null) {
                throw new IllegalArgumentException("Username and password must not be null.");
            }
        }
    }
}
