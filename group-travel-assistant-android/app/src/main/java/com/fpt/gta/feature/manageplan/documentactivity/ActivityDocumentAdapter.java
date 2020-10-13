package com.fpt.gta.feature.manageplan.documentactivity;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class ActivityDocumentAdapter extends RecyclerView.Adapter<ActivityDocumentAdapter.ViewHolder> {
    private Context mContext;
    private List<DocumentDTO> mDocumentList;
    private OnItemImageClickListener onItemImageClickListenerl;

    public ActivityDocumentAdapter(Context mContext, List<DocumentDTO> mDocumentList) {
        this.mContext = mContext;
        this.mDocumentList = mDocumentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transaction_document, parent, false);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentDTO dto = mDocumentList.get(position);
        DocumentImageUtil.loadThumbnailImage(mContext, dto, holder.imgTransactionDocument);
        holder.imgTransactionDocument.setMaxHeight(holder.imgTransactionDocument.getWidth());
        holder.lnlRowImageTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemImageClickListenerl!=null){
                    onItemImageClickListenerl.onItemImageClickListener(mDocumentList.get(position), position);
                }
            }
        });
    }

    public void notifyChangeData(List<DocumentDTO> mDtos) {
        mDocumentList = new ArrayList<>();
        mDocumentList = mDtos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int count = (mDocumentList != null) ? mDocumentList.size() : 0;
        return count;    }

    public interface OnItemImageClickListener{
        void onItemImageClickListener(DocumentDTO documentDTO, int position);
    }

    public void setOnItemImageClickListenerl(OnItemImageClickListener onItemImageClickListenerl) {
        this.onItemImageClickListenerl = onItemImageClickListenerl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lnlRowImageTransaction;
        ImageView imgTransactionDocument;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTransactionDocument = (ImageView) itemView.findViewById(R.id.imgTransactionDocument);
            lnlRowImageTransaction = (LinearLayout) itemView.findViewById(R.id.lnlTransactionDocument);
        }
    }
}
