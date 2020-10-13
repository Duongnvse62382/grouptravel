package com.fpt.gta.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fpt.gta.data.dto.DocumentDTO;
import com.google.firebase.storage.FirebaseStorage;

public final class ImageLoaderUtil {
    public static void loadImage(Context context, String uri, ImageView documentImageView) {
        if (uri != null) {
            FirebaseStorage.getInstance().getReferenceFromUrl(uri).getDownloadUrl().addOnCompleteListener(task -> {
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    if (task.isSuccessful()) {
                        if (!(activity.isFinishing() || activity.isDestroyed())) {
                            Glide.with(context).load(task.getResult()).into(documentImageView);
                        }
                    }
                }
            });
        }
    }
}
