package com.fpt.gta.model.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fpt.gta.config.AmazonConfig;
import com.fpt.gta.exception.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AmazonS3BlobServiceImpl {
    AmazonS3 amazonS3;

    @Autowired
    public AmazonS3BlobServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String putObject(String filePath, MultipartFile multipartFile, String contentType) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(multipartFile.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(AmazonConfig.BUCKET_NAME,
                    filePath,
                    multipartFile.getInputStream(),
                    objectMetadata
            );
            amazonS3.putObject(putObjectRequest);
            return amazonS3.getUrl(AmazonConfig.BUCKET_NAME, filePath).toExternalForm();
        } catch (IOException e) {
            throw new UnprocessableEntityException(e);
        }
    }

    public String putObject(String filePath, String uri, String contentType) {
        RestTemplate restTemplate = new RestTemplate();
        byte[] imageBytes = restTemplate.getForObject(uri, byte[].class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(imageBytes.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(AmazonConfig.BUCKET_NAME,
                filePath,
                byteArrayInputStream,
                objectMetadata
        );

        amazonS3.putObject(putObjectRequest);
        return amazonS3.getUrl(AmazonConfig.BUCKET_NAME, filePath).toExternalForm();
    }

}
