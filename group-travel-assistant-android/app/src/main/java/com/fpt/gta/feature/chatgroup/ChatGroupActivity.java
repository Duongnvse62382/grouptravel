package com.fpt.gta.feature.chatgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.DocumentDTO;
import com.fpt.gta.data.dto.LastMessageTime;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.FriendlyMessage;
import com.fpt.gta.feature.managegroup.documentgroup.DocumentGroupManageActivity;
import com.fpt.gta.feature.managetransaction.edittransaction.EditTransactionDocumentActivity;
import com.fpt.gta.presenter.CreateGroupDocumentPresenter;
import com.fpt.gta.util.DatabaseHelper;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.HandleFile;
import com.fpt.gta.util.KProgressHUDManager;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.fpt.gta.view.CreateGroupDocumentView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatGroupActivity extends AppCompatActivity implements CreateGroupDocumentView {
    //    private DatabaseHelper databaseHelper;
    public static final String MESSAGES_CHILD = "messages";
    public static final String MEMBERS_CHILD = "members";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
//    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private static final String LOADING_IMAGE_URL = "https://dothimoilehongphong.com/wp-content/uploads/2019/10/dang-cap-nhat.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername, mUserId;
    private Date currentTime;
    private String mPhotoUrl;
    private String contentType, txtUri;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
//    private FirebaseFirestore database;
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView, imgChatBack;
    private Map<String, MemberDTO.PersonDTO> personMap = new HashMap<>();
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorage;
    private DatabaseReference mFirebaseDatabaseReference;

    private DatabaseReference mMemberReference;
    private Long messageTime;
    private CreateGroupDocumentPresenter mCreateGroupDocumentPresenter;
    private int groupId;
    private String message_count;
    private List<MemberDTO> memberDTOList;
    private String imageUrl;
    private List<FriendlyMessage> messageList = Collections.synchronizedList(new ArrayList<>());
    private ChatGroupAdapter mChatGroupAdapter;
    private int mTotalItemCount = 0;
    private FirebaseDatabase databaseMember;
    private int mLastVisibleItemPosition;
    private boolean mIsLoading = false;
    private int mPostsPerPage = 20;
    private FirebaseDatabase databaseMemberBadges;
    private ValueEventListener newMessageTimeListener;
    private ValueEventListener initMessageTimeListener;
    private long newestMessageTimeStamp;
    private DatabaseReference membersRef;
    private DatabaseReference listenerMemberBadges;

    private long oldestMessageTimeStamp;
    //    private long idLastMessageTime;
//    private List<LastMessageTime> lastMessageTimeList;
    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        initView();
        initData();
    }

    public void initView() {
//        databaseHelper = new DatabaseHelper(this);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        imgChatBack = findViewById(R.id.imgChatBack);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (newMessageTimeListener != null) {
//                mFirebaseDatabaseReference.removeEventListener(newMessageTimeListener);
                query.removeEventListener(newMessageTimeListener);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        memberDTOList = (List<MemberDTO>) bundle.getSerializable("MEMBERLIST");
//        idLastMessageTime = (long) bundle.getSerializable("idLastMessageTime");
        groupId = SharePreferenceUtils.getIntSharedPreference(ChatGroupActivity.this, GTABundle.IDGROUP);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD);
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mChatGroupAdapter = new ChatGroupAdapter(messageList, ChatGroupActivity.this, memberDTOList);
        listenerMemberBadges = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child("listener").child("reloadMemberBadges");
        membersRef = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MEMBERS_CHILD).child(mUserId).child("lastestReadMessage");
        query = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD).orderByChild("messageTime")
                .limitToLast(20);
        imgChatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerInitMessage();
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeZone tz = TimeZone.getDefault();
                String timez = tz.getID();
                Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                messageTime = dateNoti.getTime();
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        messageTime,
                        mUserId,
                        null /* no image */);
                mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");
                registerNewMessage();
            }
        });

        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else if (!checkPermission()) {
                    requestPermission();
                }
            }
        });

        mMessageRecyclerView.setAdapter(mChatGroupAdapter);

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeAccepted && cameraAccepted & readAccepted) {
                        Toast.makeText(this, "All permissions are accepted", Toast.LENGTH_SHORT).show();
                        initData();
                    } else {
//                        Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ChatGroupActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    final Uri uri = data.getData();
                    boolean check = true;
                    check = HandleFile.checkFileUpload(ChatGroupActivity.this, data.getData());
                    if (check == true) {
                        txtUri = uri.toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Are you sure to send this image?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final KProgressHUD khub = KProgressHUDManager.showProgressBar(ChatGroupActivity.this);
                                String fileName = Instant.now().toString()
                                        + "-"
                                        + Thread.currentThread().getId()
                                        + "-"
                                        + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                                StorageReference fileToUpload = mStorage.child("chat-image/").child(String.valueOf(groupId)).child(fileName);
                                fileToUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                txtUri = uri.toString();
                                                TimeZone tz = TimeZone.getDefault();
                                                String timez = tz.getID();
                                                Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                                                messageTime = dateNoti.getTime();
                                                FriendlyMessage tempMessage = new FriendlyMessage(null, messageTime, mUserId, txtUri);
                                                mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD).push()
                                                        .setValue(tempMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        khub.dismiss();
                                                        registerNewMessage();
                                                    }
                                                });
                                            }
                                        });
                                        KProgressHUDManager.dismiss(ChatGroupActivity.this, khub);
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(this, "File upload lager than 15mbs", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private void putImageInStorage(StorageReference storageReference, Uri uri,
                                   final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatGroupActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(ChatGroupActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        TimeZone tz = TimeZone.getDefault();
                                                        String timez = tz.getID();
                                                        Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                                                        messageTime = dateNoti.getTime();
                                                        FriendlyMessage friendlyMessage =
                                                                new FriendlyMessage(null, messageTime, mUserId,
                                                                        task.getResult().toString());
                                                        mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD).push()
                                                                .setValue(friendlyMessage);
                                                    }
                                                }
                                            });
                        } else {
//                            Log.w(TAG, "Image upload task was not successful.",
                            task.getException();
                        }
                    }
                });
    }


    public void registerInitMessage() {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    messageList.add(dataSnapshot.getValue(FriendlyMessage.class));
                }
                if (messageList.size() > 0) {
                    newestMessageTimeStamp = messageList.get(messageList.size() - 1).getMessageTime();
                    membersRef.setValue(newestMessageTimeStamp);
                    oldestMessageTimeStamp = messageList.get(0).getMessageTime();
                    mChatGroupAdapter = new ChatGroupAdapter(messageList, ChatGroupActivity.this, memberDTOList);
                    mMessageRecyclerView.setAdapter(mChatGroupAdapter);
                    mChatGroupAdapter.setOnItemClickListener(new ChatGroupAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(FriendlyMessage friendlyMessage, int position) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatGroupActivity.this);
                            builder.setMessage("Are you sure to upload this file?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    contentType = "image/jpeg";
                                    createGroupDocument(friendlyMessage.getImageUrl());
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                    mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (!recyclerView.canScrollVertically(-1)) {
                                registerOldMessage();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChatGroupActivity.this, "No Message", Toast.LENGTH_SHORT).show();
                }
                registerNewMessage();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                {
                }
            }
        });
    }

    public void createGroupDocument(String txtUri) {
        DocumentDTO dto = new DocumentDTO();
        dto.setContentType(contentType);
        dto.setUri(txtUri);
        mCreateGroupDocumentPresenter = new CreateGroupDocumentPresenter(ChatGroupActivity.this, this);
        mCreateGroupDocumentPresenter.createGroupDocument(groupId, dto);
    }

    public void registerNewMessage() {
        if (query != null) {
            if (newMessageTimeListener != null) {
                query.removeEventListener(newMessageTimeListener);
            }
        }
        newMessageTimeListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<FriendlyMessage> newMessageList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                        if (friendlyMessage.getMessageTime() > newestMessageTimeStamp) {
                            newMessageList.add(friendlyMessage);
                        }
                    }
                    messageList.addAll(newMessageList);
                    newestMessageTimeStamp = messageList.get(messageList.size() - 1).getMessageTime();
                    membersRef.setValue(newestMessageTimeStamp);
                    TimeZone tz = TimeZone.getDefault();
                    String timez = tz.getID();
                    try {
                        Date dateNoti = ZonedDateTimeUtil.convertDateFromTimeZoneToUtc(new Date(), timez);
                        Long change = dateNoti.getTime();
                        listenerMemberBadges.setValue(change);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    boolean isAtLast = false;
                    if (!mMessageRecyclerView.canScrollVertically(1)) {
                        isAtLast = true;
                    }
                    mChatGroupAdapter.notifyDataSetChanged();
                    if (isAtLast) {
                        mMessageRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void registerOldMessage() {
        Query query;
        query = mFirebaseDatabaseReference.child(String.valueOf(groupId)).child(MESSAGES_CHILD).orderByChild("messageTime")
                .endAt(oldestMessageTimeStamp, "messageTime")
                .limitToLast(20);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FriendlyMessage message;
                List<FriendlyMessage> oldMessageList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    if (friendlyMessage.getMessageTime() < oldestMessageTimeStamp) {
                        oldMessageList.add(friendlyMessage);
                    }
                }
                messageList.addAll(0, oldMessageList);
                oldestMessageTimeStamp = messageList.get(0).getMessageTime();
                mChatGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public void createGroupDocumentSuccess(String messageSuccess) {

    }

    @Override
    public void createGroupDocumentFail(String messageFail) {

    }
}






