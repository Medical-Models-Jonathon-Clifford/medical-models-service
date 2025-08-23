package org.jono.medicalmodelsservice.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.config.MinioConfigHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final MinioConfigHolder minioConfig;

    @PostConstruct
    public void init() {
        try {
            if (bucketDoesNotExist()) {
                makeBucket();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error initializing MinIO", e);
        }
    }

    private boolean bucketDoesNotExist() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        return !minioClient.bucketExists(BucketExistsArgs.builder()
                                                 .bucket(minioConfig.getBucketName())
                                                 .build());
    }

    private void makeBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        minioClient.makeBucket(MakeBucketArgs.builder()
                                       .bucket(minioConfig.getBucketName()).build());
    }

    public String uploadImage(final MultipartFile file) throws Exception {
        final String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        minioClient.putObject(PutObjectArgs.builder()
                                      .bucket(minioConfig.getBucketName())
                                      .object(fileName)
                                      .stream(file.getInputStream(), file.getSize(), -1)
                                      .contentType(file.getContentType())
                                      .build());

        return fileName;
    }

    public byte[] downloadImage(final String fileName) throws Exception {
        try (final InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                                                                      .bucket(minioConfig.getBucketName())
                                                                      .object(fileName)
                                                                      .build())) {
            return stream.readAllBytes();
        }
    }
}
