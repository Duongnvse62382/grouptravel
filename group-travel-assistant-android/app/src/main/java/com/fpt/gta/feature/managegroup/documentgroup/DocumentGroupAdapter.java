package com.fpt.gta.feature.managegroup.documentgroup;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class DocumentGroupAdapter extends RecyclerView.Adapter<DocumentGroupAdapter.ViewHolder> {
    private List<DocumentDTO> documentList;
    private Context mContext;
    private String contentType = null, pdfString;
    private Uri imageUri;
    private OnItemPDFClickListener onItemPDFClickListener;
    private OnItemImageClickListener onItemImageClickListener;

    public DocumentGroupAdapter(List<DocumentDTO> documentList, Context mContext) {
        this.documentList = documentList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document_file, parent, false);
        return new ViewHolder(view);
    }


    public void notifyChangeData(List<DocumentDTO> mDtos){
        documentList = new ArrayList<>();
        documentList =mDtos;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentDTO dto = documentList.get(position);
                DocumentImageUtil.loadThumbnailImage(mContext, dto, holder.documentImage);
                holder.documentImage.setMaxHeight(holder.documentImage.getWidth());
                holder.lnlImageDocument.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemImageClickListener!=null){
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
        private ImageView imgPdfView;
        private ImageView documentImage;
        private LinearLayout lnlPDFDocument, lnlImageDocument;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            documentImage = itemView.findViewById(R.id.imgRowDocumentImage);
            lnlImageDocument = itemView.findViewById(R.id.lnlImageDocument);
        }
    }

    public void setOnItemPDFClickListener(OnItemPDFClickListener onItemPDFClickListener) {
        this.onItemPDFClickListener = onItemPDFClickListener;
    }

    public interface OnItemPDFClickListener{
        void onItemPDFClickListener(DocumentDTO documentDTO, int position);
    }

    public interface OnItemImageClickListener{
        void onItemImageClickListener(DocumentDTO documentDTO, int position);
    }

    public void setOnItemImageClickListener(OnItemImageClickListener onItemImageClickListener) {
        this.onItemImageClickListener = onItemImageClickListener;
    }
}



