package com.fpt.gta.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileOutputStream;

public class PdfHelper {

//    public static void generateImageFromPdf(Uri pdfUri, Context mContext) {
//        int pageNumber = 0;
//        PdfiumCore pdfiumCore = new PdfiumCore(mContext);
//        try {
//            ParcelFileDescriptor fd = mContext.getContentResolver().openFileDescriptor(pdfUri, "r");
//            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
//            pdfiumCore.openPage(pdfDocument, pageNumber);
//            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
//            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
//            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
//            saveImage(bmp);
//            pdfiumCore.closeDocument(pdfDocument); // important!
//        } catch (Exception e) {
//            //todo with exception
//        }
//    }
//
//
//    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
//
//    private void saveImage(Bitmap bmp) {
//        FileOutputStream out = null;
//        try {
//            File folder = new File(FOLDER);
//            if (!folder.exists())
//                folder.mkdirs();
//            File file = new File(folder, "PDF.png");
//            out = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//        } catch (Exception e) {
//            //todo with exception
//        } finally {
//            try {
//                if (out != null)
//                    out.close();
//            } catch (Exception e) {
//                //todo with exception
//            }
//        }
//    }
}
