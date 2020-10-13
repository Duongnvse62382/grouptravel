package com.fpt.gta.feature.managetransaction.addtransaction.OCRTransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.TaskDTO;
import com.fpt.gta.feature.managetransaction.edittransaction.CustomeTransactionAdapter;
import com.fpt.gta.util.ChangeValue;
import com.google.android.gms.vision.text.Line;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OcrTransactionAdapter extends RecyclerView.Adapter<OcrTransactionAdapter.ViewHolder> {
    private List<BigDecimal> lines;
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    public OcrTransactionAdapter(List<BigDecimal> lines, Context mContext) {
        this.lines = lines;
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ocr_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtMoneyResult.setText(ChangeValue.formatCurrency(lines.get(position)));
        holder.lnlRowOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClickListener(lines.get(position), position);
                }
            }
        });

    }

    public void notifyDataChange(List<BigDecimal> dtoList) {
        lines = new ArrayList<>();
        lines = dtoList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return lines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMoneyResult, txtMaybeTotal;
        LinearLayout lnlMaybeTotal, lnlRowOCR;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMoneyResult = itemView.findViewById(R.id.txtMoneyValue);
            lnlRowOCR = itemView.findViewById(R.id.lnlRowOCR);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(BigDecimal line, int position);
    }
}
