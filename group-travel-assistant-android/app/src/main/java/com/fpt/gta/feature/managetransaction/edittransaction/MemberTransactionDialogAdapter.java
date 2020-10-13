package com.fpt.gta.feature.managetransaction.edittransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.feature.manageplan.overviewplan.MemberDialogAdapter;

import java.util.List;

public class MemberTransactionDialogAdapter extends RecyclerView.Adapter<MemberTransactionDialogAdapter.ViewHolder>{
    private Context mContext;
    private OnItemEditClickListener itemEditClickListener;
    private List<MemberDTO> memberDTOList;

    public MemberTransactionDialogAdapter(Context mContext, List<MemberDTO> memberDTOList) {
        this.mContext = mContext;
        this.memberDTOList = memberDTOList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.item_dialog_memberplan, parent, false );
        return new ViewHolder (view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rdMemberPlan.setText ( memberDTOList.get ( position ).getPerson ().getName ());
        holder.lnlRowMemberDialog.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if(itemEditClickListener!=null){
                    itemEditClickListener.onItemEditClickListener ( memberDTOList.get ( position ), position );
                }
            }
        } );
    }

    @Override
    public int getItemCount() {
          int count = (memberDTOList != null) ? memberDTOList.size () : 0;
        return count;
    }


    public void setOnEditItemClickListener(OnItemEditClickListener listener) {
        this.itemEditClickListener = listener;
    }

    public interface OnItemEditClickListener {
        void onItemEditClickListener(MemberDTO memberDTO, int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rdMemberPlan;
        LinearLayout lnlRowMemberDialog;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            rdMemberPlan = itemView.findViewById ( R.id.rdMemberPlan );
            lnlRowMemberDialog = itemView.findViewById ( R.id.lnlRowMemberDialog );
        }
    }


}
