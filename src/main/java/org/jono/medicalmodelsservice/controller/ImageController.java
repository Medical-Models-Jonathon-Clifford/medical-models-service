package org.jono.medicalmodelsservice.controller;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.service.MinioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
  private final MinioService minioService;

  @PostMapping
  public ResponseEntity<String> uploadImage(@RequestParam("file") final MultipartFile file) {
    try {
      final String fileName = minioService.uploadImage(file);
      return ResponseEntity.ok(fileName);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to upload image: " + e.getMessage());
    }
  }

  @GetMapping("/{fileName}")
  public ResponseEntity<byte[]> downloadImage(@PathVariable final String fileName) {
    try {
      final byte[] data = minioService.downloadImage(fileName);
      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(data);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
