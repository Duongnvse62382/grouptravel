package com.fpt.gta.feature.managetransaction.edittransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.CurrencyDTO;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdaper extends RecyclerView.Adapter<CurrencyAdaper.CurrencyViewHolder> implements SectionIndexer {
    private List<CurrencyDTO> currencyDTOList;
    private Context mContext;
    private ArrayList<Integer> mSectionPositions;
    private OnItemClickListener mListener;

    public CurrencyAdaper(List<CurrencyDTO> currencyDTOList, Context mContext) {
        this.currencyDTOList = currencyDTOList;
        this.mContext = mContext;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = currencyDTOList.size(); i < size; i++) {
            String section = String.valueOf(currencyDTOList.get(i).getCode().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);

    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_currency, parent, false);
        CurrencyViewHolder currencyViewHolder = new CurrencyViewHolder(view);
        return currencyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        holder.txtCode.setText(currencyDTOList.get(position).getCode());
        holder.txtName.setText(currencyDTOList.get(position).getName());
        holder.mLnlRootCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(currencyDTOList.get(position), position);
                }
            }
        });
    }

    public void OnItemClickListener(OnItemClickListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(CurrencyDTO currencyDTO, int pos);
    }

    @Override
    public int getItemCount() {
        return currencyDTOList.size();
    }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder {
        TextView txtCode, txtName;
        LinearLayout mLnlRootCurrency;

        public CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCode = itemView.findViewById(R.id.tvCode);
            txtName = itemView.findViewById(R.id.tvName);
            mLnlRootCurrency = itemView.findViewById(R.id.mLnlRootCurrency);
        }
    }
}
