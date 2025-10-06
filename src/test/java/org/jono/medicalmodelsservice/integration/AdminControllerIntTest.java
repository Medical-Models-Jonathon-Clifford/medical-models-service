package org.jono.medicalmodelsservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jono.medicalmodelsservice.model.UserSupportSearchParams;
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
class AdminControllerIntTest {

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
    void shouldReturnDocumentRankingsForCompany() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns the document rankings for company 2.
        this.mockMvc.perform(get("/admin/users/documents/ranking")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("Dr. Gregory House")))
                .andExpect(jsonPath("$[0].frequency", is(4)))
                .andExpect(jsonPath("$[1].name", is("Dr. James Wilson")))
                .andExpect(jsonPath("$[1].frequency", is(2)))
                .andExpect(jsonPath("$[2].name", is("Dr. Lisa Cuddy")))
                .andExpect(jsonPath("$[2].frequency", is(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnCommentRankingsForCompany() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns the comment rankings for company 2.
        this.mockMvc.perform(get("/admin/users/comments/ranking")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Dr. James Wilson")))
                .andExpect(jsonPath("$[0].frequency", is(2)))
                .andExpect(jsonPath("$[1].name", is("Dr. Gregory House")))
                .andExpect(jsonPath("$[1].frequency", is(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnModelRankingsForCompany() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns the model rankings for company 2.
        this.mockMvc.perform(get("/admin/company/models/ranking")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].type", is("text")))
                .andExpect(jsonPath("$[0].frequency", is(14)))
                .andExpect(jsonPath("$[1].type", is("dielectric")))
                .andExpect(jsonPath("$[1].frequency", is(7)))
                .andExpect(jsonPath("$[2].type", is("half-life")))
                .andExpect(jsonPath("$[2].frequency", is(7)))
                .andExpect(jsonPath("$[3].type", is("image")))
                .andExpect(jsonPath("$[3].frequency", is(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnDocumentMetricsForCompany() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns document metrics for company 2.
        this.mockMvc.perform(get("/admin/company/documents/metrics")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.total", is(7)))
                .andExpect(jsonPath("$.dailyCounts", hasSize(279)))
                .andExpect(jsonPath("$.dailyCounts[0].date", is("2025-01-01")))
                .andExpect(jsonPath("$.dailyCounts[0].newResources", is(1)))
                .andExpect(jsonPath("$.dailyCounts[0].runningTotal", is(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnCommentMetricsForCompany() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns comment metrics for company 2.
        this.mockMvc.perform(get("/admin/company/comments/metrics")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.total", is(3)))
                .andExpect(jsonPath("$.dailyCounts", hasSize(255)))
                .andExpect(jsonPath("$.dailyCounts[0].date", is("2025-01-25")))
                .andExpect(jsonPath("$.dailyCounts[0].newResources", is(3)))
                .andExpect(jsonPath("$.dailyCounts[0].runningTotal", is(3)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnAllUsersWithNoSearchParams() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns all users where the search term is empty.
        final var searchParams = new UserSupportSearchParams("");
        final var requestBody = objectMapper.writeValueAsString(searchParams);
        this.mockMvc.perform(post("/admin/companies/users/search")
                                     .content(requestBody)
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is("4")))
                .andExpect(jsonPath("$[0].name", is("Dr. Lisa Cuddy")))
                .andExpect(jsonPath("$[0].email", is("lcuddy@princetonplainsboro.org")))
                .andExpect(jsonPath("$[1].id", is("5")))
                .andExpect(jsonPath("$[1].name", is("Dr. Gregory House")))
                .andExpect(jsonPath("$[1].email", is("ghouse@princetonplainsboro.org")))
                .andExpect(jsonPath("$[2].id", is("6")))
                .andExpect(jsonPath("$[2].name", is("Dr. James Wilson")))
                .andExpect(jsonPath("$[2].email", is("jwilson@princetonplainsboro.org")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnUserMatchingSearchParam() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Returns users with names matching the search term "Lisa".
        final var searchParams = new UserSupportSearchParams("Lisa");
        final var requestBody = objectMapper.writeValueAsString(searchParams);
        this.mockMvc.perform(post("/admin/companies/users/search")
                                     .content(requestBody)
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("4")))
                .andExpect(jsonPath("$[0].name", is("Dr. Lisa Cuddy")))
                .andExpect(jsonPath("$[0].email", is("lcuddy@princetonplainsboro.org")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "lcuddy")
    void shouldReturnCompanyDetails() throws Exception {
        when(authUtils.extractCompanyId(any(), any())).thenReturn("2");

        // Return details for company 2.
        this.mockMvc.perform(get("/admin/company/details")
                                     .header("Authorization", TEST_JWT)
                                     .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is("2")))
                .andExpect(jsonPath("$.name", is("House MD Centre for Superheroes")))
                .andExpect(jsonPath("$.locationState", is("QLD")))
                .andExpect(jsonPath("$.logoFilename", is("house-md-logo-1.png")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
