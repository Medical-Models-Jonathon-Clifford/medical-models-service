package org.jono.medicalmodelsservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jono.medicalmodelsservice.model.DocumentState;
import org.jono.medicalmodelsservice.model.dto.UpdateDocumentDto;
import org.jono.medicalmodelsservice.utils.AuthenticationUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(IntegrationTestConfig.class)
class DocumentControllerIntTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TEST_JWT = "Bearer " + TestJwtUtils.createTestJwtToken(
            "lcuddy",
            "lcuddy@example.com",
            "2"
    );

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");
    static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z");

    @MockitoBean
    AuthenticationUtils authUtils;

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
    void shouldReturnDocument() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns document 1.
        this.mockMvc.perform(get("/documents/1")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Raven has no wings left")))
                .andExpect(jsonPath("$.createdDate", is("2025-01-01T12:00:00")))
                .andExpect(jsonPath("$.modifiedDate", is("2025-01-01T12:00:00")))
                .andExpect(jsonPath("$.body", isA(String.class)))
                .andExpect(jsonPath("$.creator", is("4")))
                .andExpect(jsonPath("$.creatorFullName", is("Dr. Lisa Cuddy")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldCreateNewDocumentWithParent() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");
        when(authUtils.extractUserId(any(), any())).thenReturn("4");

        // Creates a new document with parent id 1.
        this.mockMvc.perform(post("/documents/new")
                                     .queryParam("parentId", "1")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is("14")))
                .andExpect(jsonPath("$.title", is(nullValue())))
                .andExpect(jsonPath("$.createdDate", is(nullValue())))
                .andExpect(jsonPath("$.modifiedDate", is(nullValue())))
                .andExpect(jsonPath("$.body", is(nullValue())))
                .andExpect(jsonPath("$.creator", is("4")))
                .andExpect(jsonPath("$.state", is("DRAFT")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldUpdateDocument() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");
        when(authUtils.extractUserId(any(), any())).thenReturn("4");
        final var updateDocumentDto = new UpdateDocumentDto("A bird hero has no wings left", null,
                                                            DocumentState.ACTIVE);
        final var requestBody = objectMapper.writeValueAsString(updateDocumentDto);

        // Updates the title of document 1.
        this.mockMvc.perform(put("/documents/1")
                                     .content(requestBody)
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("A bird hero has no wings left")))
                .andExpect(jsonPath("$.state", is("ACTIVE")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldGetDocumentNavigationTree() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns the document navigation tree for company 2.
        this.mockMvc.perform(get("/documents/all/navigation")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id", is("2")))
                .andExpect(jsonPath("$[0].title", is("Dr Strange is losing his hair")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
