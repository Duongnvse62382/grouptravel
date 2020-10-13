package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ConflictException;
import com.fpt.gta.exception.InternalServerErrorException;
import com.fpt.gta.exception.UnprocessableEntityException;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.*;
import com.fpt.gta.model.service.BlobService;
import com.fpt.gta.model.service.DocumentService;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    GroupRepository groupRepository;
    ActivityRepository activityRepository;
    DocumentRepository documentRepository;
    DocumentWithActivityRepository documentWithActivityRepository;
    DocumentWithTransactionRepository documentWithTransactionRepository;
    TransactionRepository transactionRepository;
    BlobService blobService;
    ForkJoinPool forkJoinPool;

    @Autowired
    public DocumentServiceImpl(GroupRepository groupRepository, ActivityRepository activityRepository, DocumentRepository documentRepository, DocumentWithActivityRepository documentWithActivityRepository, DocumentWithTransactionRepository documentWithTransactionRepository, TransactionRepository transactionRepository, BlobService blobService, ForkJoinPool forkJoinPool) {
        this.groupRepository = groupRepository;
        this.activityRepository = activityRepository;
        this.documentRepository = documentRepository;
        this.documentWithActivityRepository = documentWithActivityRepository;
        this.documentWithTransactionRepository = documentWithTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.blobService = blobService;
        this.forkJoinPool = forkJoinPool;
    }

    private static final String baseBlobPath = "blob";

    // Group Document
    @Override
    public List<Document> findAllDocumentGroup(String firebaseUid, Integer idGroup) {
        Group group = groupRepository.findById(idGroup).get();
        group.getDocumentList().size();
        List<Document> documentListResult = new ArrayList<>();

        for (Document document : group.getDocumentList()) {
            for (DocumentWithTransaction documentWithTransaction : document.getDocumentWithTransactionList()) {
                boolean isAllowView = false;
                for (TransactionDetail transactionDetail : documentWithTransaction.getTransaction().getTransactionDetailList()) {
                    if (transactionDetail.getMember() == null) {
                        isAllowView = true;
                        break;
                    } else if (transactionDetail.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                        isAllowView = true;
                        break;
                    }
                }
                if (isAllowView) {
                    documentListResult.add(document);
                    break;
                }
            }
        }

        prepareDocumentDownloadLink(documentListResult);
        return group.getDocumentList();
    }

    @Override
    public Document addNewDocumentGroup(String firebaseUid, Integer idGroup, String contentType, String uri) {
        try {
            Group group = groupRepository.findById(idGroup).get();

            RestTemplate restTemplate = new RestTemplate();
            URI uriObject = null;
            uriObject = new URI(uri);

            ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(uriObject, byte[].class);
            byte[] originByteArray = responseEntity.getBody();

            String fileName = baseBlobPath
                    + "/"
                    + idGroup.toString()
                    + "/"
                    + Instant.now().toString()
                    + "-"
                    + Thread.currentThread().getId()
                    + "-"
                    + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

            String thumbnailUrl = null;
            String url = null;
            //make thumbnail
            byte[] thumbnailByteArray = null;
            if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                thumbnailByteArray = getImageThumbnail(originByteArray);

            } else if (contentType.equals("application/pdf")) {
                thumbnailByteArray = getImageThumbnail(getPdfImage(originByteArray));
            } else {
                throw new UnprocessableEntityException("not support content type");
            }

            String[] urlArray = new String[2];

            byte[] finalThumbnailByteArray = thumbnailByteArray;
            IntStream.of(1, 2).parallel().forEach(flag -> {
                if (flag == 1) {
                    urlArray[0] = blobService.putObject(
                            fileName
                                    + "-"
                                    + "Thumbnail",
                            "image/jpeg",
                            finalThumbnailByteArray);
                }
                if (flag == 2) {

                    urlArray[1] = blobService.putObject(
                            fileName,
                            contentType,
                            originByteArray);
                }
            });

            thumbnailUrl = urlArray[0];
            url = urlArray[1];

            Document document = new Document();
            document.setUri(url);
            document.setThumbnailUri(thumbnailUrl);
            document.setContentType(contentType);
            document.setGroup(group);
            return documentRepository.save(document);
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException("URISyntaxException");
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public Document addNewDocumentGroup(Integer idGroup, String contentType, byte[] data) {
        try {
            Group group = groupRepository.findById(idGroup).get();
            byte[] responseByteArray = data;

            String fileName = baseBlobPath
                    + "/"
                    + idGroup.toString()
                    + "/"
                    + Instant.now().toString()
                    + "-"
                    + Thread.currentThread().getId()
                    + "-"
                    + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);

            String thumbnailUrl = null;
            String url = null;
            //make thumbnail
            byte[] thumbnailByteArray = null;
            if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                thumbnailByteArray = getImageThumbnail(data);

            } else if (contentType.equals("application/pdf")) {
                thumbnailByteArray = getImageThumbnail(getPdfImage(data));
            } else {
                throw new UnprocessableEntityException("not support content type");
            }

            String[] urlArray = new String[2];

            byte[] finalThumbnailByteArray = thumbnailByteArray;
            IntStream.of(1, 2).parallel().forEach(flag -> {
                if (flag == 1) {
                    urlArray[0] = blobService.putObject(
                            fileName
                                    + "-"
                                    + "Thumbnail",
                            "image/jpeg",
                            finalThumbnailByteArray);
                }
                if (flag == 2) {

                    urlArray[1] = blobService.putObject(
                            fileName,
                            contentType,
                            data);
                }
            });

            thumbnailUrl = urlArray[0];
            url = urlArray[1];

            Document document = new Document();
            document.setUri(url);
            document.setThumbnailUri(thumbnailUrl);
            document.setContentType(contentType);
            document.setGroup(group);
            return documentRepository.save(document);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
    }

    private byte[] getImageThumbnail(byte[] imageBytes) throws IOException {
        BufferedImage originalImage = null;
        InputStream targetStream = null;
        try {
            targetStream = new ByteArrayInputStream(imageBytes);
            originalImage = ImageIO.read(targetStream);
            int height = originalImage.getHeight();
            int width = originalImage.getWidth();
            height = height > 480 ? 480 : height;
            width = width > 640 ? 640 : width;
            BufferedImage bufferedImage = Thumbnails.of(originalImage).size(width, height).asBufferedImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            targetStream.close();
            return imageInByte;
        } catch (OutOfMemoryError outOfMemoryError) {
            originalImage = null;
            targetStream = null;
            System.gc();
            outOfMemoryError.printStackTrace();
            return imageBytes;
        }
    }

    private byte[] getPdfImage(byte[] pdfBytes) throws IOException {
        InputStream targetStream = new ByteArrayInputStream(pdfBytes);
        PDDocument document = PDDocument.load(targetStream);

        //Instantiating the PDFRenderer class
        PDFRenderer renderer = new PDFRenderer(document);
        //Rendering an image from the PDF document
        BufferedImage bufferedImage = renderer.renderImage(0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        targetStream.close();
        document.close();
        return imageInByte;
    }

    @Override
    public void deleteGroupDocument(String firebaseUid, Integer idDocument) {
        Document document = documentRepository.findById(idDocument).get();
        if (document.getDocumentWithActivityList().size() != 0 || document.getDocumentWithTransactionList().size() != 0) {
            throw new ConflictException();
        }
        documentRepository.deleteById(idDocument);
        try {
            BlobInfo blobInfo = blobService.getReferenceFromUrl(document.getUri());
            BlobInfo blobThumbnailInfo = blobService.getReferenceFromUrl(document.getThumbnailUri());
            blobService.removeObject(blobInfo.getName());
            blobService.removeObject(blobThumbnailInfo.getName());
        } catch (Exception e) {
            System.out.println("Remove blob on delete fail");
        }
    }

    // Transaction Document
    @Override
    public List<Document> findAllDocumentTransaction(String firebaseUid, Integer idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction).get();
        List<Document> result = new ArrayList<>();
        for (DocumentWithTransaction transactionWithDocument : transaction.getDocumentWithTransactionList()) {
            result.add(transactionWithDocument.getDocument());
        }
        prepareDocumentDownloadLink(result);
        return result;
    }

    @Override
    public void addGroupDocumentToTransaction(String firebaseUid, Integer idTransaction, Integer idDocument) {
        Transaction transaction = transactionRepository.findById(idTransaction).get();
        addGroupDocumentToTransaction(firebaseUid, transaction, idDocument);
    }

    @Override
    public void addGroupDocumentToTransaction(String firebaseUid, Transaction transaction, Integer idDocument) {
        Document document = documentRepository.findById(idDocument).get();
        boolean isAlreadyHave = false;
        if (transaction.getDocumentWithTransactionList() != null) {
            for (DocumentWithTransaction transactionWithDocument : transaction.getDocumentWithTransactionList()) {
                if (transactionWithDocument.getDocument().getId().compareTo(idDocument) == 0) {
                    isAlreadyHave = true;
                }
            }
        }
        if (isAlreadyHave) {
//            throw new ConflictException("already have");
        } else {
            DocumentWithTransaction documentWithTransaction = new DocumentWithTransaction();
            documentWithTransaction.setDocument(document);
            documentWithTransaction.setTransaction(transaction);
            documentWithTransactionRepository.save(documentWithTransaction);
        }
    }

    @Override
    public void addNewDocumentTransaction(String firebaseUid, Integer idTransaction, String contentType, String uri) {
        Transaction transaction = transactionRepository.findById(idTransaction).get();
        addNewDocumentTransaction(firebaseUid, transaction, contentType, uri);
    }

    @Override
//    @Async

    public void addNewDocumentTransaction(String firebaseUid, Transaction transaction, String contentType, String uri) {
        Integer idGroup = transaction.getOwner().getJoinedGroup().getId();
        Document document = addNewDocumentGroup(firebaseUid, idGroup, contentType, uri);
        DocumentWithTransaction documentWithTransaction = new DocumentWithTransaction();
        documentWithTransaction.setDocument(document);
        documentWithTransaction.setTransaction(transaction);

        documentWithTransactionRepository.save(documentWithTransaction);
//        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void deleteDocumentTransaction(String firebaseUid, Integer idDocument) {
        Document document = documentRepository.findById(idDocument).get();
        List<DocumentWithTransaction> documentWithTransactionList = document.getDocumentWithTransactionList();

        for (DocumentWithTransaction documentWithTransaction : documentWithTransactionList) {
            documentWithTransactionRepository.deleteById(documentWithTransaction.getId());
        }
    }

    // Activity Document
    @Override
    public List<Document> findAllDocumentActivity(String firebaseUid, Integer idActivity) {
        Activity activity = activityRepository.findById(idActivity).get();
        List<Document> result = new ArrayList<>();
        for (DocumentWithActivity documentWithActivity : activity.getDocumentWithActivityList()) {
            result.add(documentWithActivity.getDocument());
        }
        prepareDocumentDownloadLink(result);
        return result;
    }

    @Override
    public void addNewDocumentActivity(String firebaseUid, Integer idActivity, String contentType, String uri) {
        Activity activity = activityRepository.findById(idActivity).get();
        Integer idGroup = activity.getPlan().getTrip().getGroup().getId();
        Document document = addNewDocumentGroup(firebaseUid, idGroup, contentType, uri);
        DocumentWithActivity documentWithActivity = new DocumentWithActivity();
        documentWithActivity.setActivity(activity);
        documentWithActivity.setDocument(document);
        documentWithActivityRepository.save(documentWithActivity);
    }

    @Override
    public void addGroupDocumentToActivity(String firebaseUid, Integer idActivity, Integer idDocument) {
        Activity activity = activityRepository.findById(idActivity).get();
        Document document = documentRepository.findById(idDocument).get();
        boolean isAlreadyHave = false;
        if (activity.getDocumentWithActivityList() != null) {
            for (DocumentWithActivity documentWithActivity : activity.getDocumentWithActivityList()) {
                if (documentWithActivity.getDocument().getId().compareTo(idDocument) == 0) {
                    isAlreadyHave = true;
                }
            }
        }
        if (isAlreadyHave) {
//            throw new ConflictException("already have");
        } else {
            DocumentWithActivity documentWithActivity = new DocumentWithActivity();
            documentWithActivity.setDocument(document);
            documentWithActivity.setActivity(activity);
            documentWithActivityRepository.save(documentWithActivity);
        }
    }

    @Override
    public void deleteDocumentActivity(String firebaseUid, Integer idDocument) {
        Document document = documentRepository.findById(idDocument).get();
        List<DocumentWithActivity> documentWithActivityList = document.getDocumentWithActivityList();

        for (DocumentWithActivity documentWithActivity : documentWithActivityList) {
            documentWithActivityRepository.deleteById(documentWithActivity.getId());
        }
    }

    @Override
    public void prepareDocumentDownloadLink(List<Document> documentList) {
//        documentList.parallelStream().forEach(document -> {
//            IntStream.of(1, 2).parallel().forEach(flag -> {
//                if (flag == 1) {
//                    document.setDownloadUrl(getFirebaseDownloadUrl(document.getUri()));
//                } else if (flag == 2) {
//                    document.setDownloadThumbnailUrl(getFirebaseDownloadUrl(document.getThumbnailUri()));
//                }
//            });
//        });
    }

    private String getFirebaseDownloadUrl(String uri) {
        try {
            Storage storage = StorageClient.getInstance().bucket().getStorage();
            URI uriObject = new URI(uri);
            BlobInfo blobInfo = Blob.newBuilder(BlobId.of(uriObject.getHost(), uriObject.getPath().substring(1))).build();
            return storage.signUrl(blobInfo, 1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature()).toExternalForm();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }
}
