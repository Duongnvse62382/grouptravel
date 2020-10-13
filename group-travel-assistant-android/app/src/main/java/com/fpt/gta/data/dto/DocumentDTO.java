package com.fpt.gta.data.dto;

import android.graphics.Bitmap;
import android.net.Uri;

import com.fpt.gta.webService.gson.Exclude;

import java.io.Serializable;

import lombok.Data;

@Data
public class DocumentDTO implements Serializable {
    private Integer id;
    private String uri;
    private String contentType;
    private String downloadUrl;
    private String thumbnailUri;
    private String downloadThumbnailUrl;

    public DocumentDTO(String downloadThumbnailUrl) {
        this.downloadThumbnailUrl = downloadThumbnailUrl;
    }

    public DocumentDTO() {
    }



}
