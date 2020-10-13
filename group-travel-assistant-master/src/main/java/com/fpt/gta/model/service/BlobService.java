package com.fpt.gta.model.service;

import com.google.cloud.storage.BlobInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public interface BlobService {

    @Async
    CompletableFuture<String> putObjectAsync(String filePath, String contentType, byte[] data);

    String putObject(String filePath, String contentType, byte[] data);

    String putObject(String filePath, String contentType, MultipartFile multipartFile);

    String putObject(String filePath, String contentType, String uri);

    void removeObject(String filePath);

    BlobInfo getReferenceFromUrl(String gsUri) throws URISyntaxException;

    String resolveGsUri(String uriString);

    String getMediaLink(String filePath);

    String getSignLink(String filePath);
}
