package com.example.demo.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

@Component
public class S3Bucket {

    private static final Logger log = LoggerFactory.getLogger(S3Bucket.class);

    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Bucket(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    // Return both S3 key and presigned URL
    public S3UploadResult uploadImage(MultipartFile file) {
        try {
            String fileName = (file.getOriginalFilename() == null)
                    ? "file"
                    : Paths.get(file.getOriginalFilename()).getFileName().toString();

            String key = "images/" + UUID.randomUUID() + "-" + fileName;

            log.info("File name: {}, Key: {}", fileName, key);

            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .key(key)
                    .bucket(bucketName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Generate Presigned URL for 3 days
            GetObjectRequest getRequest = GetObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofDays(3))
                    .getObjectRequest(getRequest)
                    .build();

            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presigned.url().toString();

            log.info("Presigned URL: {}", presignedUrl);

            // Return both key and URL
            return new S3UploadResult(key, presignedUrl);

        } catch (Exception e) {
            log.error("Failed to upload image to S3 bucket", e);
            return null; // Better: throw a custom exception instead
        }
    }
}
