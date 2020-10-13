package com.fpt.gta.rest.managedocument;

import com.fpt.gta.model.service.DocumentService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.StorageClient;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    ModelMapper modelMapper;
    DocumentService documentService;

    @Autowired
    public DocumentController(ModelMapper modelMapper, DocumentService documentService) {
        this.modelMapper = modelMapper;
        this.documentService = documentService;
    }

    // Group Document
    @GetMapping("/documentGroup")
    public List<DocumentDTO> findAllDocumentInGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                                    @RequestParam String idGroup
    ) {
        List<DocumentDTO> documentDTOList = Arrays.asList(
                modelMapper.map(
                        documentService.findAllDocumentGroup(
                                firebaseToken.getUid(),
                                Integer.parseInt(idGroup)),
                        DocumentDTO[].class)
        );
        return documentDTOList;
    }

    @PostMapping("/documentGroup")
    public void addNewGroupDocument(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                    @RequestBody DocumentDTO documentDTO,
                                    @RequestParam String idGroup
    ) {
        documentService.addNewDocumentGroup(
                firebaseToken.getUid(),
                Integer.parseInt(idGroup),
                documentDTO.getContentType(),
                documentDTO.getUri()
        );
    }

    @DeleteMapping("/documentGroup")
    public void deleteGroupDocument(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                    @RequestParam String idDocument) {
        documentService.deleteGroupDocument(firebaseToken.getUid(), Integer.parseInt(idDocument));
    }

    // Transaction Document
    @GetMapping("/documentTransaction")
    public List<DocumentDTO> findAllDocumentInTransaction(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                                          @RequestParam String idTransaction) {
        List<DocumentDTO> documentDTOList = Arrays.asList(
                modelMapper.map(
                        documentService.findAllDocumentTransaction(
                                firebaseToken.getUid(),
                                Integer.parseInt(idTransaction)),
                        DocumentDTO[].class)
        );
        return documentDTOList;
    }

    @PostMapping("/documentTransaction")
    public void addNewTransactionDocument(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                          @RequestBody DocumentDTO documentDTO,
                                          @RequestParam String idTransaction
    ) {
        documentService.addNewDocumentTransaction(
                firebaseToken.getUid(),
                Integer.parseInt(idTransaction),
                documentDTO.getContentType(),
                documentDTO.getUri()
        );
    }

    @PatchMapping("/documentTransaction")
    public void addGroupDocumentToTransaction(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                              @RequestParam String idTransaction,
                                              @RequestParam String idDocument
    ) {
        documentService.addGroupDocumentToTransaction(
                firebaseToken.getUid(),
                Integer.parseInt(idTransaction),
                Integer.parseInt(idDocument)
        );
    }

    @DeleteMapping("/documentTransaction")
    public void deleteTransactionDocument(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                          @RequestParam String idDocument) {
        documentService.deleteDocumentTransaction(firebaseToken.getUid(), Integer.parseInt(idDocument));
    }

    // Activity Document
    @GetMapping("/documentActivity")
    public List<DocumentDTO> findAllDocumentInActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                                       @RequestParam String idActivity) {
        List<DocumentDTO> documentDTOList = Arrays.asList(
                modelMapper.map(
                        documentService.findAllDocumentActivity(
                                firebaseToken.getUid(),
                                Integer.parseInt(idActivity)),
                        DocumentDTO[].class)
        );
        return documentDTOList;
    }


    @PostMapping("/documentActivity")
    public void addNewActivityDocument(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                       @RequestParam String idActivity,
                                       @RequestBody DocumentDTO documentDTO
    ) {
        documentService.addNewDocumentActivity(
                firebaseToken.getUid(),
                Integer.parseInt(idActivity),
                documentDTO.getContentType(),
                documentDTO.getUri()
        );
    }

    @PatchMapping("/documentActivity")
    public void addGroupDocumentToActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                           @RequestParam String idActivity,
                                           @RequestParam String idDocument
    ) {
        documentService.addGroupDocumentToActivity(
                firebaseToken.getUid(),
                Integer.parseInt(idActivity),
                Integer.parseInt(idDocument)
        );
    }

    @DeleteMapping("/documentActivity")
    public void deleteActivityDocument(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                       @RequestParam String idDocument) {
        documentService.deleteDocumentActivity(firebaseToken.getUid(), Integer.parseInt(idDocument));
    }

}
