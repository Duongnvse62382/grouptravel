package com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SuggestAndVoteActivityAdapter extends RecyclerView.Adapter<SuggestAndVoteActivityAdapter.ViewHolder> {
    private List<SuggestedActivityResponseDTO> suggestedActivityDTOS;
    private SuggestAndVoteActivityAdapter.OnItemClickListener onItemClickListener;
    private SuggestAndVoteActivityAdapter.OnDeleteClickListenner onDeleteClickListenner;
    private SuggestAndVoteActivityAdapter.OnEditClickListenner onEditClickListenner;
    private SuggestAndVoteActivityAdapter.OnVoteSuggestedListenner onVoteSuggestedListenner;
    private SuggestAndVoteActivityAdapter.OnUnVoteSuggestedListenner onUnVoteSuggestedListenner;
    private Context mContext;
    private Integer groupStatus;
    int numberVote;

    public SuggestAndVoteActivityAdapter(List<SuggestedActivityResponseDTO> suggestedActivityDTOS, Context mContext) {
        this.suggestedActivityDTOS = suggestedActivityDTOS;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_recycleview_list_activitysuggest, parent, false);
        ViewHolder viewHolder = new SuggestAndVoteActivityAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        groupStatus = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.GROUPSTATUS);
        holder.lnlNext.setVisibility(View.GONE);
        holder.txtEndtPlace.setText("");
        holder.lnlPlaceEndSuggested.setVisibility(View.GONE);
        holder.cvImgPlaceEnd.setVisibility(View.GONE);
        String idGoogleStart = suggestedActivityDTOS.get(position).getStartPlace().getGooglePlaceId();
        String idGoogleEnd = suggestedActivityDTOS.get(position).getEndPlace().getGooglePlaceId();
        if (!idGoogleStart.equals(idGoogleEnd)) {
            holder.lnlNext.setVisibility(View.VISIBLE);
            holder.lnlPlaceEndSuggested.setVisibility(View.VISIBLE);
            holder.txtEndtPlace.setText(suggestedActivityDTOS.get(position).getEndPlace().getName());
            holder.cvImgPlaceEnd.setVisibility(View.VISIBLE);
        }

        holder.txtEndtPlace.setText(suggestedActivityDTOS.get(position).getEndPlace().getName());
        holder.txtStartPlace.setText(suggestedActivityDTOS.get(position).getStartPlace().getName());

        String idPerson = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Boolean isTooFar = suggestedActivityDTOS.get(position).getIsTooFar();

        if (isTooFar.equals(true)) {
            holder.txtIsTooFar.setVisibility(View.VISIBLE);
        } else {
            holder.txtIsTooFar.setVisibility(View.GONE);
        }

        if (!groupStatus.equals(GroupStatus.PLANNING)) {
            holder.imgUnLikeSuggestedActivity.setVisibility(View.GONE);
            holder.imgLikeSuggestedActivity.setVisibility(View.GONE);
            holder.imgEditSuggested.setVisibility(View.GONE);
            holder.imgDeleteSuggested.setVisibility(View.GONE);
            holder.txtNumberVote.setVisibility(View.GONE);
        } else {

            holder.txtNumberVote.setVisibility(View.VISIBLE);
            Boolean isLike = false;
            for (int i = 0; i < suggestedActivityDTOS.get(position).getVotedSuggestedActivityList().size(); i++) {
                String idMemberVote = suggestedActivityDTOS.get(position).getVotedSuggestedActivityList().get(i).getMember().getPerson().getFirebaseUid();
                if (idMemberVote.equals(idPerson)) {
                    isLike = true;
                    break;
                }
            }


            if (isLike.equals(true)) {
                holder.imgLikeSuggestedActivity.setVisibility(View.GONE);
                holder.imgUnLikeSuggestedActivity.setVisibility(View.VISIBLE);
            } else {
                holder.imgUnLikeSuggestedActivity.setVisibility(View.GONE);
                holder.imgLikeSuggestedActivity.setVisibility(View.VISIBLE);
            }


            try {
                String memberCreate = suggestedActivityDTOS.get(position).getOwner().getPerson().getFirebaseUid();
                if (memberCreate.equals(idPerson)) {
                    holder.imgEditSuggested.setVisibility(View.VISIBLE);
                    holder.imgDeleteSuggested.setVisibility(View.VISIBLE);

                } else {
                    holder.imgEditSuggested.setVisibility(View.GONE);
                    holder.imgDeleteSuggested.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String typeString = "";
        Integer idType = suggestedActivityDTOS.get(position).getIdType();
        if (idType == 1) {
            typeString = "Activity";
        } else if (idType == 2) {
            typeString = "Transportation";
        } else {
            typeString = "Accomodation";
        }
        numberVote = suggestedActivityDTOS.get(position).
                getVotedSuggestedActivityList().
                size();
        holder.txtNumberVote.setText(numberVote + "");
        holder.txtSuggestName.setText(suggestedActivityDTOS.get(position).
                getName());
        holder.txtType.setText(typeString);


        if (suggestedActivityDTOS.get(position).getStartPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, suggestedActivityDTOS.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), holder.imgSuggestedPlaceStart);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgSuggestedPlaceStart);
        }

        if (suggestedActivityDTOS.get(position).getEndPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, suggestedActivityDTOS.get(position).getEndPlace().getPlaceImageList().get(0).getUri(), holder.imgSuggestedPlaceEnd);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(holder.imgSuggestedPlaceEnd);
        }

        holder.lnlRowSuggestActivityItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(suggestedActivityDTOS.get(position), position);
                }
            }
        });

        holder.imgDeleteSuggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListenner != null) {
                    onDeleteClickListenner.onDeleteClickListener(suggestedActivityDTOS.get(position), position);
                }
            }
        });

        holder.imgEditSuggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListenner != null) {
                    onEditClickListenner.onEditClickListener(suggestedActivityDTOS.get(position), position);
                }
            }
        });

        holder.imgLikeSuggestedActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onVoteSuggestedListenner != null) {
                    onVoteSuggestedListenner.onVoteSugggestedListenner(suggestedActivityDTOS.get(position), position);
                    holder.imgLikeSuggestedActivity.setVisibility(View.GONE);
                    holder.imgUnLikeSuggestedActivity.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.imgUnLikeSuggestedActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUnVoteSuggestedListenner != null) {
                    onUnVoteSuggestedListenner.onUnVoteSugggestedListenner(suggestedActivityDTOS.get(position), position);
                    holder.imgUnLikeSuggestedActivity.setVisibility(View.GONE);
                    holder.imgLikeSuggestedActivity.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void notifyChangeData(List<SuggestedActivityResponseDTO> mDtos) {
        suggestedActivityDTOS = new ArrayList<>();
        suggestedActivityDTOS = mDtos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return suggestedActivityDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtSuggestName;
        private TextView txtStartPlace;
        private TextView txtEndtPlace;
        private TextView txtNumberVote;
        private TextView txtType;
        private TextView txtIsTooFar;
        private ImageView imgEditSuggested;
        private ImageView imgDeleteSuggested;
        private ImageView imgLikeSuggestedActivity;
        private ImageView imgUnLikeSuggestedActivity;
        private LinearLayout lnlRowSuggestActivityItem;
        private LinearLayout lnlNext;
        private ImageView imgSuggestedPlaceStart;
        private ImageView imgSuggestedPlaceEnd;
        private LinearLayout lnlPlaceEndSuggested;
        private CardView cvImgPlaceStart, cvImgPlaceEnd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIsTooFar = (TextView) itemView.findViewById(R.id.txtIsTooFar);
            txtSuggestName = (TextView) itemView.findViewById(R.id.txtSuggestName);
            txtStartPlace = (TextView) itemView.findViewById(R.id.txtStartPlace);
            txtEndtPlace = (TextView) itemView.findViewById(R.id.txtEndtPlace);
            imgEditSuggested = (ImageView) itemView.findViewById(R.id.imgEditSuggested);
            imgDeleteSuggested = (ImageView) itemView.findViewById(R.id.imgDeleteSuggested);
            lnlRowSuggestActivityItem = (LinearLayout) itemView.findViewById(R.id.lnlRowSuggestActivityItem);
            imgLikeSuggestedActivity = (ImageView) itemView.findViewById(R.id.imgLikeSuggestedActivity);
            imgUnLikeSuggestedActivity = (ImageView) itemView.findViewById(R.id.imgUnLikeSuggestedActivity);
            txtNumberVote = (TextView) itemView.findViewById(R.id.txtNumberVote);
            txtType = (TextView) itemView.findViewById(R.id.txtType);
            imgSuggestedPlaceStart = (ImageView) itemView.findViewById(R.id.imgSuggestedPlaceStart);
            imgSuggestedPlaceEnd = (ImageView) itemView.findViewById(R.id.imgSuggestedPlaceEnd);
            lnlNext = (LinearLayout) itemView.findViewById(R.id.lnlNext);
            lnlPlaceEndSuggested = (LinearLayout) itemView.findViewById(R.id.lnlPlaceEndSuggested);
            cvImgPlaceStart = (CardView) itemView.findViewById(R.id.cvImgPlaceStart);
            cvImgPlaceEnd = (CardView) itemView.findViewById(R.id.cvImgPlaceEnd);
            imgUnLikeSuggestedActivity.setVisibility(View.GONE);
            txtIsTooFar.setVisibility(View.GONE);
            txtSuggestName.setSelected(true);
            txtStartPlace.setSelected(true);
            txtEndtPlace.setSelected(true);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(SuggestedActivityResponseDTO suggestedActivityDTO, int position);
    }

    public void setOnItemClickListener(SuggestAndVoteActivityAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnDeleteClickListenner {
        void onDeleteClickListener(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position);
    }

    public void setOnDeleteClickListenner(SuggestAndVoteActivityAdapter.OnDeleteClickListenner onDeleteClickListenner) {
        this.onDeleteClickListenner = onDeleteClickListenner;
    }

    public interface OnEditClickListenner {
        void onEditClickListener(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position);
    }

    public void setOnEditClickListenner(SuggestAndVoteActivityAdapter.OnEditClickListenner onEditClickListenner) {
        this.onEditClickListenner = onEditClickListenner;
    }

    public interface OnVoteSuggestedListenner {
        void onVoteSugggestedListenner(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position);
    }

    public void setOnVoteSuggestedListenner(SuggestAndVoteActivityAdapter.OnVoteSuggestedListenner onVoteSuggestedListenner) {
        this.onVoteSuggestedListenner = onVoteSuggestedListenner;
    }

    public interface OnUnVoteSuggestedListenner {
        void onUnVoteSugggestedListenner(SuggestedActivityResponseDTO suggestedActivityResponseDTO, int position);

    }

    public void setOnUnVoteSuggestedListenner(SuggestAndVoteActivityAdapter.OnUnVoteSuggestedListenner onUnVoteSuggestedListenner) {
        this.onUnVoteSuggestedListenner = onUnVoteSuggestedListenner;
    }


}



