package com.fpt.gta.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fpt.gta.data.dto.DocumentDTO;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.storage.FirebaseStorage;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public final class DocumentImageUtil {

    public static void loadThumbnailImage(Context context, DocumentDTO documentDTO, ImageView documentImageView) {
    if (documentDTO.getDownloadThumbnailUrl() == null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(documentDTO.getThumbnailUri()).getDownloadUrl().addOnCompleteListener(task -> {
                documentDTO.setDownloadThumbnailUrl(task.getResult().toString());
                if (context instanceof Activity){
                    Activity activity = (Activity)context;
                    if (!activity.isFinishing()){
                        Glide.with(context).load(documentDTO.getDownloadThumbnailUrl()).into(documentImageView);
                    }
                }
            });
        }
    else {
            Glide.with(context).load(documentDTO.getDownloadThumbnailUrl()).into(documentImageView);
        }

    }

    public static void loadImage(Context context, DocumentDTO documentDTO, ImageView documentImageView) {
        if (documentDTO.getDownloadUrl() == null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(documentDTO.getUri()).getDownloadUrl().addOnCompleteListener(task -> {
                documentDTO.setDownloadUrl(task.getResult().toString());
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {
                        Glide.with(context).load(documentDTO.getDownloadUrl()).into(documentImageView);
                    }
                }
            });
        } else {
            Glide.with(context).load(documentDTO.getDownloadUrl()).into(documentImageView);
        }
    }

    public static void loadPdf(DocumentDTO documentDTO, PDFView pdfView) {
        if (documentDTO.getDownloadUrl() == null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(documentDTO.getUri()).getDownloadUrl().addOnCompleteListener(task -> {
                documentDTO.setDownloadUrl(task.getResult().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            InputStream input = new URL(documentDTO.getDownloadUrl()).openStream();

                            pdfView.fromStream(input).load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            });
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        InputStream input = new URL(documentDTO.getDownloadUrl()).openStream();
                        pdfView.fromStream(input).load();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }

    public static CompletableFuture<Bitmap> getPdfThumbnailBitmap(Context context, ContentResolver contentResolver, Uri pdfUri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int pageNumber = 0;
                PdfiumCore pdfiumCore = new PdfiumCore(context);
                ParcelFileDescriptor fd = contentResolver.openFileDescriptor(pdfUri, "r");
                PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
                pdfiumCore.openPage(pdfDocument, pageNumber);
                int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
                int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
                pdfiumCore.closeDocument(pdfDocument); // important!
                return bmp;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
