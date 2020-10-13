package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ConflictException;
import com.fpt.gta.exception.InternalServerErrorException;
import com.fpt.gta.exception.UnprocessableEntityException;
import com.fpt.gta.model.service.BlobService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageBlobServiceImpl implements BlobService {

    @Override
    @Async
    public CompletableFuture<String> putObjectAsync(String filePath, String contentType, byte[] data) {
        StorageClient storageClient = StorageClient.getInstance();
        Blob blob = storageClient.bucket().create(filePath, data, contentType);
        return CompletableFuture.completedFuture(getGoogleStorageUriFromBlob(blob));
    }

    @Override
    public String putObject(String filePath, String contentType, byte[] data) {
        StorageClient storageClient = StorageClient.getInstance();
        Blob blob = storageClient.bucket().create(filePath, data, contentType);
        return getGoogleStorageUriFromBlob(blob);
    }

    @Override
    public String putObject(String filePath, String contentType, MultipartFile multipartFile) {
        try {
            byte[] content = multipartFile.getBytes();
            return putObject(filePath, contentType, content);
        } catch (IOException e) {
            throw new UnprocessableEntityException(e);
        }
    }

    @Override
    public String putObject(String filePath, String contentType, String uri) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uriObject = new URI(uri);
            ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(uriObject, byte[].class);
            byte[] imageBytes = responseEntity.getBody();
            return putObject(filePath, contentType, imageBytes);
        } catch (URISyntaxException e) {
            throw new ConflictException("invalid uri");
        }

    }

    @Override
    public void removeObject(String filePath) {
        StorageClient storageClient = StorageClient.getInstance();
        Blob blob = storageClient.bucket().get(filePath, Storage.BlobGetOption.generationNotMatch(0));
        if (blob != null) {
            blob.delete(Blob.BlobSourceOption.generationMatch());
        } else {
            System.out.println("Blob:" + filePath + " not found");
        }
    }

    @Override
    public BlobInfo getReferenceFromUrl(String gsUri) throws URISyntaxException {
        URI uriObject = new URI(gsUri);
        BlobInfo blobInfo = Blob.newBuilder(BlobId.of(uriObject.getHost(), uriObject.getPath().substring(1))).build();
        return blobInfo;
    }

    @Override
    public String resolveGsUri(String uriString) {
        if (uriString.startsWith("gs://")) {
            return "https://storage.googleapis.com/" + uriString.substring(5);
        } else {
            return "";
        }
    }

    private String getUriFromBlob(Blob blob) {
        URL url = blob.signUrl(90, TimeUnit.DAYS, Storage.SignUrlOption.withContentType().withV2Signature());
        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(url.toURI());
            uriBuilder.removeQuery();
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url.getProtocol() + "://" + url.getHost() + url.getPath();
    }

    private String getGoogleStorageUriFromBlob(Blob blob) {
        try {
            URIBuilder uriBuilder = new URIBuilder();
            URI uri = uriBuilder.setScheme("gs").setHost(blob.getBucket()).setPath(blob.getName()).build();
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public String getMediaLink(String filePath) {
        StorageClient storageClient = StorageClient.getInstance();
        Blob blob = storageClient.bucket().get(filePath);
        return blob.getMediaLink();
    }

    @Override
    public String getSignLink(String filePath) {
        StorageClient storageClient = StorageClient.getInstance();
        Blob blob = storageClient.bucket().get(filePath);
        return blob.signUrl(90, TimeUnit.DAYS, Storage.SignUrlOption.withContentType().withV2Signature()).toExternalForm();
    }
}
