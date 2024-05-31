package org.aionys.main.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
public final class RequestBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;
    private Object body;
    private String credentialsAsBase;

    private CompletableFuture<String> bearer;

    private Authorization authorization = Authorization.BEARER;

    private boolean authorize = false;

    public RequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private void clearBearer() {
        if (bearer != null) {
            bearer.cancel(true);
            bearer = null;
        }
    }

    /**
     * Sets the credentials for the request. If the credentials are already set, they will be overridden.
     * If the authorization type is set to bearer, the request will be sent
     * to the /login endpoint to retrieve the bearer token.
     * @param credentials the credentials
     * @return this builder
     */
    public RequestBuilder credentials(@NonNull Credentials credentials) {
        this.authorize = true;
        if (bearer != null) {
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
     * @param authorization the authorization type
     * @return this builder
     */
    public RequestBuilder authorization(@NonNull Authorization authorization) {
        if (bearer != null && authorization != Authorization.BEARER) {
            log.warn("Authorization type has already been set to bearer. It will be overridden.");
            clearBearer();
        }
        this.authorization = authorization;
        return this;
    }

    private void retrieveBearer() {
        bearer = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return mockMvc.perform(post("/login")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        credentialsAsBase
                                )).andReturn().getResponse().getContentAsString();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private String getAuthorizationHeader() throws Exception {
        return switch (authorization) {
            case BEARER -> "Bearer " + bearer.get();
            case BASIC -> credentialsAsBase;
        };
    }

    /**
     * Sets the body for the request. If the body is already set, it will be overridden.
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
     * @param requestBuilder the request builder
     * @return the result actions
     * @throws Exception if an during the request occurs
     */
    public ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        if (authorize) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader());
        }
        if (body != null) {
            requestBuilder.contentType("application/json");
            requestBuilder.content(objectMapper.writeValueAsString(body));
        }
        return mockMvc.perform(requestBuilder);
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
