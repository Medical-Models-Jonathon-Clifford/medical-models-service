package org.jono.medicalmodelsservice.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.jono.medicalmodelsservice.service.MinioService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
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
class ImageControllerErrorIntTest {

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");
    static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z");

    @MockitoBean
    private MinioService minioService;

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
    @DirtiesContext
    void shouldReturn500WhenMinioThrowsExceptionOnUpload() throws Exception {
        when(minioService.uploadImage(any())).thenThrow(new RuntimeException("Test exception"));

        // Create a test image
        final byte[] imageContent = "This is a test image content".getBytes(StandardCharsets.UTF_8);
        final var imageFile = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_JPEG_VALUE,
                imageContent
        );

        // Returns 500 Internal Server Error when MinIO throws an exception during upload.
        this.mockMvc.perform(multipart("/images")
                                     .file(imageFile)
                                     .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "lcuddy")
    @DirtiesContext
    void shouldReturn500WhenMinioThrowsExceptionOnDownload() throws Exception {
        when(minioService.downloadImage(any())).thenThrow(new RuntimeException("Test exception"));

        // Returns 500 Internal Server Error when MinIO throws an exception during download.
        this.mockMvc.perform(get("/images/{fileName}", "test-image.png"))
                .andExpect(status().isInternalServerError());
    }
}
