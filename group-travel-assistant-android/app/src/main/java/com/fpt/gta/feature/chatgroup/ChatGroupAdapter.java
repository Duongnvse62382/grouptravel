package com.fpt.gta.feature.chatgroup;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.FriendlyMessage;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.MessageViewHolder> {
    private String imageUrl;
    private List<FriendlyMessage> messageList;
    private final int MESSAGE_IN_VIEW_TYPE = 1;
    private final int MESSAGE_OUT_VIEW_TYPE = 2;
    private Context mContext;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private List<MemberDTO> memberDTOList;
    private Map<String, MemberDTO.PersonDTO> personMap = new HashMap<>();
    private OnItemClickListener itemClickListener;


    public ChatGroupAdapter() {
    }

    public ChatGroupAdapter(List<FriendlyMessage> messageList, Context mContext, List<MemberDTO> memberDTOList) {
        this.messageList = messageList;
        this.mContext = mContext;
        this.memberDTOList = memberDTOList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == MESSAGE_IN_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_in, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_out, parent, false);
        }
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        if (messageList.get(position).getText() != null) {
            holder.messageTextView.setText(messageList.get(position).getText());
            holder.messageTextView.setVisibility(TextView.VISIBLE);
            holder.messageTime.setVisibility(TextView.VISIBLE);
            holder.messageImageView.setVisibility(ImageView.GONE);
            holder.imgShareMessage.setVisibility(CircleImageView.GONE);
        } else if (messageList.get(position).getImageUrl() != null) {
            imageUrl = messageList.get(position).getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(holder.messageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(holder.messageImageView);
                                } else {
                                    Toast.makeText(mContext, "Not Success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Glide.with(holder.messageImageView.getContext())
                        .load(messageList.get(position).getImageUrl())
                        .into(holder.messageImageView);
            }
            holder.messageTime.setVisibility(TextView.VISIBLE);
            holder.messageImageView.setVisibility(ImageView.VISIBLE);
            holder.messageTextView.setVisibility(TextView.GONE);
            holder.imgShareMessage.setVisibility(CircleImageView.VISIBLE);
            holder.imgShareMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClickListener(messageList.get(position), position);
                    }

                }
            });

        }
        for (MemberDTO memberDTO : memberDTOList) {
            personMap.putIfAbsent(memberDTO.getPerson().getFirebaseUid(), memberDTO.getPerson());
        }

        Long date = messageList.get(position).getMessageTime();
        Date localDate = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(new Date(date), ZoneId.systemDefault().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        holder.messageTime.setText(simpleDateFormat.format(localDate));
//                viewHolder.messengerTextView.setText(personMap.get(friendlyMessage.getFirebaseUid()).getName());
        Glide.with(mContext)
                .load(personMap.get(messageList.get(position).getFirebaseUid()).getPhotoUri())
                .into(holder.messengerImageView);
    }

    @Override
    public int getItemCount() {
        int count = (messageList != null) ? messageList.size() : 0;
        return count;
    }


    @Override
    public int getItemViewType(int position) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        if (messageList.get(position).getFirebaseUid().equals(mUserId)) {
            return MESSAGE_OUT_VIEW_TYPE;
        }
        return MESSAGE_IN_VIEW_TYPE;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView messageTextView;
        ImageView messageImageView;
        TextView messageTime;
        CircleImageView messengerImageView, imgShareMessage;
        LinearLayout lnlRowMessage;

        public MessageViewHolder(View v) {
            super(v);
            mView = v;
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messageTime = (TextView) itemView.findViewById(R.id.messengerTime);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            imgShareMessage = (CircleImageView) itemView.findViewById(R.id.imgShareMessage);
            lnlRowMessage = (LinearLayout) itemView.findViewById(R.id.lnlRowMessage);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(FriendlyMessage friendlyMessage, int position);
    }


}
