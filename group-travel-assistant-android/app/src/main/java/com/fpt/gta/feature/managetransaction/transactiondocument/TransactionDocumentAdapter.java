package com.fpt.gta.feature.managetransaction.transactiondocument;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.util.DocumentImageUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

public class TransactionDocumentAdapter extends RecyclerView.Adapter<TransactionDocumentAdapter.ViewHolder> {
    private List<DocumentDTO> documentList = new ArrayList<>();
    private Context mContext;
    private String contentType = null, pdfString;
    private Uri imageUri;
    private OnItemPDFClickListener onItemPDFClickListener;
    private OnItemImageClickListener onItemImageClickListener;
    private String pdfFileName;
    private Integer pageNumber = 0;


    public TransactionDocumentAdapter(List<DocumentDTO> documentList, Context mContext) {
        this.documentList = documentList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transaction_document, parent, false);
        return new ViewHolder(view);
    }

    public void notifyChangeData(List<DocumentDTO> mDtos) {
        documentList = new ArrayList<>();
        documentList = mDtos;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentDTO dto = documentList.get(position);
        DocumentImageUtil.loadThumbnailImage(mContext, dto, holder.imgRowDocumentTransactionImage);
        holder.imgRowDocumentTransactionImage.setMaxHeight(holder.imgRowDocumentTransactionImage.getWidth());
        holder.lnlImageTransactionDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemImageClickListener != null) {
                    onItemImageClickListener.onItemImageClickListener(dto, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        int count = (documentList != null) ? documentList.size() : 0;
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRowDocumentTransactionImage, imgPDFVIEW;

        private LinearLayout lnlImageTransactionDocument;
        //        private PDFView pdfView;
        private LinearLayout lnlPDF;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRowDocumentTransactionImage = itemView.findViewById(R.id.imgTransactionDocument);
//            imgPDFVIEW = itemView.findViewById(R.id.imgPDFVIEW);
            lnlImageTransactionDocument = itemView.findViewById(R.id.lnlTransactionDocument);
//            lnlPDF = itemView.findViewById(R.id.lnlPDF);
//            pdfView = itemView.findViewById(R.id.PdfView);
        }
    }

    public void setOnItemPDFClickListener(OnItemPDFClickListener onItemPDFClickListener) {
        this.onItemPDFClickListener = onItemPDFClickListener;
    }

    public interface OnItemPDFClickListener {
        void onItemPDFClickListener(DocumentDTO documentDTO, int position);
    }

    public interface OnItemImageClickListener {
        void onItemImageClickListener(DocumentDTO documentDTO, int position);
    }

    public void setOnItemImageClickListener(OnItemImageClickListener onItemImageClickListener) {
        this.onItemImageClickListener = onItemImageClickListener;
    }

}
