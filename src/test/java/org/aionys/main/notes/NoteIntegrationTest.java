package org.aionys.main.notes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aionys.main.exceptionhandling.FieldError;
import org.aionys.main.utils.RequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.aionys.main.utils.RequestBuilder.Credentials;
import static org.aionys.main.utils.RequestBuilder.Credentials.VALID_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String CONTROLLER_PATH = "/notes";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static List<Credentials> credentialsSource() {
        return List.of(
                VALID_CREDENTIALS,
                new Credentials("user2", "123Ffg%1!")
        );
    }

    private static Map<String, List<Note>> ownerUsernameToNotesSource() {
        var notes = List.of(
                new Note("Note 1", "Content 1"),
                new Note("Note 2", "Content 2"),
                new Note("Note 3", "Content 3")
        );
        var notesWithIds = IntStream.range(0, notes.size())
                .mapToObj(i -> {
                    var note = notes.get(i);
                    note.setId((long) i + 1);
                    return note;
                }).toList();
        return Map.of(
                "user1", notesWithIds.subList(0, 2),
                "user2", notesWithIds.subList(2, 3)
        );
    }

    private static Stream<Arguments> credentialsToNotesSource() {
        var credentials = credentialsSource();
        return ownerUsernameToNotesSource().entrySet().stream()
                .map(entry -> Arguments.of(
                        credentials.stream().filter(c -> c.username().equals(entry.getKey())).findFirst().orElseThrow(),
                        entry.getValue()
                ));
    }


    @ParameterizedTest
    @SuppressWarnings("all") // suppress warning about record exposed beyond visibility scope
    @MethodSource("credentialsToNotesSource")
    public void testGetAll(Credentials credentials, List<Note> notes) throws Exception {
        var receivedContent = new RequestBuilder(mockMvc)
                .credentials(credentials)
                .perform(get(CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        var receivedNotes = objectMapper
                .readValue(receivedContent.getResponse().getContentAsString(), GetNoteDTO[].class);

        assertThat(receivedNotes).usingRecursiveFieldByFieldElementComparatorOnFields(
                "id", "title", "content"
        ).containsExactlyInAnyOrderElementsOf(
                notes.stream()
                        .map(note -> new GetNoteDTO(note.getId(), note.getTitle(), note.getContent(), null, null))
                        .toList()
        );
    }

    @Test
    public void testGetAll_unauthorized() throws Exception {
        new RequestBuilder(mockMvc)
                .perform(get(CONTROLLER_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetById_valid() throws Exception {
        var receivedContent = new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .perform(get(CONTROLLER_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var receivedNote = objectMapper
                .readValue(receivedContent.getResponse().getContentAsString(), GetNoteDTO.class);

        assertThat(receivedNote).usingRecursiveComparison()
                .comparingOnlyFields("id", "title", "content")
                .isEqualTo(
                        new GetNoteDTO(1L, "Note 1", "Content 1", null, null)
                );
    }

    @ParameterizedTest
    @CsvSource({
            "4,Invalid note id",
            "3,Another owner"
    })
    public void testGetById_notFound(long id, String reason) throws Exception {
        new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .perform(get(CONTROLLER_PATH + "/" + id))
                .andExpect(result -> assertEquals(404, result.getResponse().getStatus(), reason));
    }

    @Test
    public void testGetById_anotherOwner() throws Exception {
        new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .perform(get(CONTROLLER_PATH + "/3"))
                .andExpect(result -> assertEquals(404, result.getResponse().getStatus(), "Another owner"));
    }

    @Test
    @DirtiesContext
    public void testDeleteById_valid() throws Exception {
        new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .perform(delete(CONTROLLER_PATH + "/1"))
                .andExpect(status().isNoContent());

        new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .perform(get(CONTROLLER_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "4,Invalid note id",
            "3,Another owner"
    })
    @DirtiesContext
    public void testDeleteById_notFound(long id, String reason) throws Exception {
        new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .perform(delete(CONTROLLER_PATH + "/" + id))
                .andExpect(result -> assertEquals(404, result.getResponse().getStatus(), reason));
    }

    private static Stream<Arguments> invalidNotesAndErrors() {
        return Stream.of(
                Arguments.of(
                        new PostNoteDTO("", "Content"),
                        List.of(new FieldError("must not be blank", "title", ""))
                ),
                Arguments.of(
                        new PostNoteDTO("Title", "   "),
                        List.of(new FieldError("must not be blank", "content", "   "))
                )
        );
    }

    @ParameterizedTest
    @SuppressWarnings("all") // suppress warning about record exposed beyond visibility scope
    @MethodSource("invalidNotesAndErrors")
    @DirtiesContext
    public void testCreate_invalid(PostNoteDTO note, List<FieldError> expectedErrors) throws Exception {
        var response = new RequestBuilder(mockMvc)
                .credentials(VALID_CREDENTIALS)
                .body(note)
                .perform(post(CONTROLLER_PATH))
                .andExpect(status().isBadRequest())
                .andReturn();

        var receivedErrors = objectMapper
                .readValue(response.getResponse().getContentAsString(), FieldError[].class);

        assertThat(receivedErrors).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedErrors);
    }
}
