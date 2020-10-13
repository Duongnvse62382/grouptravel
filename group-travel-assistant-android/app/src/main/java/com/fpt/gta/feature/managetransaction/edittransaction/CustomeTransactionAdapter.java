package com.fpt.gta.feature.managetransaction.edittransaction;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.MemberTransactionDTO;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.feature.managetransaction.addtransaction.AddTransactionActivity;
import com.fpt.gta.util.ChangeValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CustomeTransactionAdapter extends RecyclerView.Adapter<CustomeTransactionAdapter.ViewHolder> {
    private List<TransactionDTO.TransactionDetailDTO> participantList;
    private List<MemberTransactionDTO> memberTransactionDTOList;
    private OnItemTransactionClickListener onItemTransactionClickListener;
    private OnItemValueMoneyClickListener onItemValueMoneyClickListener;
    private Context mContext;
    private CustomeTransactionAdapter mAddMoneyAdapter;
    private TransactionDTO.TransactionDetailDTO payerDetailDTO;
    private TransactionDTO.TransactionDetailDTO groupShareDetailDTO;

    public CustomeTransactionAdapter(List<TransactionDTO.TransactionDetailDTO> participantList, Context mContext,
                                     TransactionDTO.TransactionDetailDTO payerDetailDTO,
                                     TransactionDTO.TransactionDetailDTO groupShareDetailDTO
    ) {
        this.participantList = participantList;
        this.mContext = mContext;
        this.payerDetailDTO = payerDetailDTO;
        this.groupShareDetailDTO = groupShareDetailDTO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_transaction_for_who, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtMemberTransactionForWho.setText(participantList.get(position).getMember().getPerson().getName());
        if (participantList.get(position).isSelected()) {
            holder.cbMemberTransactionForWho.setChecked(true);
        } else {
            holder.cbMemberTransactionForWho.setChecked(false);
        }
        holder.cbMemberTransactionForWho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemValueMoneyClickListener != null) {
                    if (holder.cbMemberTransactionForWho.isChecked()) {
                        participantList.get(position).setSelected(true);
                        divideAmount();
                    } else {
                        participantList.get(position).setAmount(BigDecimal.ZERO);
                        participantList.get(position).setModified(false);
                        participantList.get(position).setSelected(false);
                        divideAmount();
                    }
                    onItemValueMoneyClickListener.onItemValueMoneyClickListener(participantList.get(position), position);
                }
            }
        });
        String cleanString = ChangeValue.formatBigCurrency(participantList.get(position).getAmount());
        holder.edtMoneyForEachMember.setText(cleanString);
        holder.edtMoneyForEachMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemTransactionClickListener != null) {
                    onItemTransactionClickListener.onItemTransactionClickListener(participantList.get(position), position);

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        int count = (participantList != null) ? participantList.size() : 0;
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbMemberTransactionForWho;
        private TextView edtMoneyForEachMember;
        private TextView txtMemberTransactionForWho;
        private LinearLayout lnlCheckBoxEditTransaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edtMoneyForEachMember = itemView.findViewById(R.id.edtMoneyForEachMember);
            lnlCheckBoxEditTransaction = itemView.findViewById(R.id.lnlRowCheckBoxPaidBy);
            cbMemberTransactionForWho = itemView.findViewById(R.id.cbMemberTransactionForWho);
            txtMemberTransactionForWho = itemView.findViewById(R.id.txtNameMemberTransactionForWho);
        }
    }

    public void notifyChange(List<TransactionDTO.TransactionDetailDTO> transactionDetailDTOList) {
        participantList = new ArrayList<>();
        participantList = transactionDetailDTOList;
        notifyDataSetChanged();
    }

    public void divideAmount() {
        BigDecimal sumAmount = payerDetailDTO.getAmount().abs().setScale(2, RoundingMode.DOWN)
                .subtract(groupShareDetailDTO.getAmount().abs().setScale(2, RoundingMode.DOWN));
        Toast.makeText(mContext, "Divided ", Toast.LENGTH_SHORT).show();
        List<TransactionDTO.TransactionDetailDTO> unSelectedList = new ArrayList<>();
        List<TransactionDTO.TransactionDetailDTO> selectedList = new ArrayList<>();
        List<TransactionDTO.TransactionDetailDTO> modifiedSelectedList = new ArrayList<>();
        List<TransactionDTO.TransactionDetailDTO> unModifiedSelectedList = new ArrayList<>();
        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
            if (transactionDetailDTO.isSelected()) {
                selectedList.add(transactionDetailDTO);
                if (transactionDetailDTO.isModified()) {
                    modifiedSelectedList.add(transactionDetailDTO);
                } else {
                    unModifiedSelectedList.add(transactionDetailDTO);
                }
            } else {
                unSelectedList.add(transactionDetailDTO);
            }
        }

        BigDecimal sumAmountAfterMinusModified = sumAmount;
        for (TransactionDTO.TransactionDetailDTO modifiedSelectedDetailDTO : modifiedSelectedList) {
            sumAmountAfterMinusModified = sumAmountAfterMinusModified.subtract(modifiedSelectedDetailDTO.getAmount().abs());
        }
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionDTO.TransactionDetailDTO unModifiedSelectedDetailDTO : unModifiedSelectedList) {
            BigDecimal temp = sumAmountAfterMinusModified.divide(BigDecimal.valueOf(unModifiedSelectedList.size()), BigDecimal.ROUND_DOWN);
            unModifiedSelectedDetailDTO.setAmount(temp);
            total = total.add(temp);
        }
        if (total.compareTo(sumAmountAfterMinusModified) < 0 && unModifiedSelectedList.size() > 1) {
            TransactionDTO.TransactionDetailDTO firstTransactionDetailDTO = unModifiedSelectedList.get(0);
            firstTransactionDetailDTO.setAmount(
                    firstTransactionDetailDTO.getAmount().add(
                            sumAmountAfterMinusModified.subtract(total).abs()
                    )
            );
        }

        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : unSelectedList) {
            transactionDetailDTO.setAmount(BigDecimal.ZERO);
        }

        notifyChange(participantList);
    }


    public void setOnItemTransactionClickListener(OnItemTransactionClickListener onItemTransactionClickListener) {
        this.onItemTransactionClickListener = onItemTransactionClickListener;
    }

    public interface OnItemTransactionClickListener {
        void onItemTransactionClickListener(TransactionDTO.TransactionDetailDTO transactionDetailDTO, int postion);
    }


    public void setOnItemValueMoneyClickListener(OnItemValueMoneyClickListener onItemValueMoneyClickListener) {
        this.onItemValueMoneyClickListener = onItemValueMoneyClickListener;
    }

    public interface OnItemValueMoneyClickListener {
        void onItemValueMoneyClickListener(TransactionDTO.TransactionDetailDTO transactionDetailDTO, int position);
    }
}
