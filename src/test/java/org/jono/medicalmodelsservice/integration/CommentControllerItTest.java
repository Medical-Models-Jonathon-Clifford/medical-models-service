package org.jono.medicalmodelsservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommentControllerItTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");
    static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z");

    @BeforeAll
    static void beforeAll() {
        mysql.start();
        minio.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
        minio.stop();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("minio.endpoint", minio::getS3URL);
        registry.add("minio.accessKey", minio::getUserName);
        registry.add("minio.secretKey", minio::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnCreatedComment() throws Exception {
        // Check the initial state. Should have no comments for document 1.
        this.mockMvc.perform(get("/comments/documents/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Create a new comment in document 1.
        final var newComment = new NewComment("1", "test body 1", "4", null);
        final var requestBody = objectMapper.writeValueAsString(newComment);
        this.mockMvc.perform(post("/comments")
                                     .content(requestBody)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("12")))
                .andExpect(jsonPath("$.documentId", is("1")))
                .andExpect(jsonPath("$.creator", is("4")))
                .andExpect(jsonPath("$.body", is("test body 1")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Check the final state. Should have 1 comment for document 1.
        this.mockMvc.perform(get("/comments/documents/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].comment.body", is("test body 1")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "spotter")
    void shouldEditComment() throws Exception {
        // Check the initial state. The first comment for document 11 should have the initial text.
        this.mockMvc.perform(get("/comments/documents/11").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].comment.body", is("Earth or extraterrestrial?")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Edit comment 10.
        final var editCommentDto = new EditCommentDto("Earth, extraterrestrial or subterranean?");
        final var requestBody = objectMapper.writeValueAsString(editCommentDto);
        this.mockMvc.perform(put("/comments/10")
                                     .content(requestBody)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body", is("Earth, extraterrestrial or subterranean?")));

        // Check the final state. The first comment for document 11 should have the updated text.
        this.mockMvc.perform(get("/comments/documents/11").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].comment.body", is("Earth, extraterrestrial or subterranean?")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "spotter")
    void shouldReturnNotFoundWhenEditingCommentThatDoesNotExist() throws Exception {
        // Attempt to edit comment 15 which does not exist. Should return 404.
        final var editCommentDto = new EditCommentDto("Earth, extraterrestrial or subterranean?");
        final var requestBody = objectMapper.writeValueAsString(editCommentDto);
        this.mockMvc.perform(put("/comments/15")
                                     .content(requestBody)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "spotter")
    void shouldDeleteComment() throws Exception {
        // Check the initial state. Should be 2 comments for document 11.
        this.mockMvc.perform(get("/comments/documents/11").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Delete comment 10.
        this.mockMvc.perform(delete("/comments/10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Check the final state. Should be 1 comment for document 11.
        this.mockMvc.perform(get("/comments/documents/11").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
