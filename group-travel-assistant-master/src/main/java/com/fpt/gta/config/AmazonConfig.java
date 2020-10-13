package com.fpt.gta.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fpt.gta.util.StringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {
    public static final String BUCKET_NAME="s3-group-travel-assistant-blob";

    AWSCredentials credentials = new BasicAWSCredentials(
            StringUtil.decodeBase64("QUtJQUk0QkY0VUVCRjVRRFZIR1E="),
            StringUtil.decodeBase64("TWYrRHpSUUNaUzNocmRXNURPWGQrWGU0VVovNWZONkx3VFdLeEF4Sw==")
    );

    @Bean
    public AmazonS3 theAmazonS3ClientBean() {
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
        return s3client;
    }
}