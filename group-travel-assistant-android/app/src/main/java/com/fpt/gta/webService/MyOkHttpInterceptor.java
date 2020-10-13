package com.fpt.gta.webService;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fpt.gta.App;
import com.fpt.gta.MainActivity;
import com.fpt.gta.feature.authentication.AuthenticationActivity;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyOkHttpInterceptor implements Interceptor {

    public MyOkHttpInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = getTokenFirebase();
        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .method(original.method(), original.body())
                .build();
        Response response = chain.proceed(request);
        if (response.code() == 403) {
            Context context = App.getAppContext();
            Intent intent = new Intent(context, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        return response;
    }

    public String getTokenFirebase() {
        final StringBuilder token = new StringBuilder();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    try {
                        long expirationTimestamp = task.getResult().getExpirationTimestamp();
                        long issuedTimestamp = task.getResult().getIssuedAtTimestamp();
                        long currentTimestamp = Instant.now().toEpochMilli();
                        if (expirationTimestamp - currentTimestamp < 5 * 60 * 1000) {
                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        token.append(task.getResult().getToken());
                                    }
                                    countDownLatch.countDown();
                                }
                            });
                        } else {
                            token.append(task.getResult().getToken());
                            countDownLatch.countDown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                countDownLatch.await(30L, TimeUnit.SECONDS);
                return token.toString();
            } catch (InterruptedException ie) {
                return null;
            }
        }
        return null;
    }

}
