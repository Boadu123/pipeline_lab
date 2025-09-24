package com.example.demo.service;

import com.example.demo.models.Photos;
import com.example.demo.repository.PhotoRepository;
import com.example.demo.utility.S3Bucket;
import com.example.demo.utility.S3UploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final S3Bucket s3Bucket;
    private final String cloudfrontDomain;

    public PhotoService(PhotoRepository photoRepository,
                        S3Bucket s3Bucket,
                        @Value("${CLOUDFRONT_DOMAIN}") String cloudfrontDomain) {
        this.photoRepository = photoRepository;
        this.s3Bucket = s3Bucket;
        this.cloudfrontDomain = cloudfrontDomain;
    }

    public Photos uploadPhoto(MultipartFile file, String description) {
        // 1. Upload to S3 and get the object key
        S3UploadResult uploadResult = s3Bucket.uploadImage(file);
        if (uploadResult == null) {
            throw new RuntimeException("Failed to upload image to S3");
        }

        // 2. Create Photo entity and save only the key and description
        Photos photo = new Photos();
        photo.setDescription(description);
        photo.setS3Key(uploadResult.getKey());

        // 3. Save to database
        return photoRepository.save(photo);
    }

    public List<Photos> getAllPhotos() {
        // 1. Get all photo records from the database
        List<Photos> photos = photoRepository.findAll(); // Assuming you have this method

        // 2. For each photo, construct its public CloudFront URL
        return photos.stream().map(photo -> {
            // URL format is https://<cloudfront_domain>/<s3_object_key>
            String publicUrl = "https://" + cloudfrontDomain + "/" + photo.getS3Key();
            photo.setDisplayUrl(publicUrl);
            return photo;
        }).collect(Collectors.toList());
    }
}