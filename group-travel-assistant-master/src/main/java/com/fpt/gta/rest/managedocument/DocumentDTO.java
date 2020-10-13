package com.fpt.gta.rest.managedocument;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

@Data

public class DocumentDTO {
    private Integer id;
    private String uri;
    private String thumbnailUri;
    private String contentType;
    private String downloadThumbnailUrl;
    private String downloadUrl;
}
