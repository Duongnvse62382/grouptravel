package com.fpt.gta.config;

import com.fpt.gta.util.StringUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    public static final String BUCKET_NAME = "group-travel-assistant.appspot.com";

    @EventListener(ApplicationReadyEvent.class)
    void createFireBaseApp() throws IOException {

//        Resource resource = new ClassPathResource("serviceAccountKey.json");

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(getServiceAccountKeyStream(serviceAccountKey)))
                .setStorageBucket(BUCKET_NAME)
                .setDatabaseUrl("https://group-travel-assistant.firebaseio.com/")
                .setConnectTimeout(60000)
                .setReadTimeout(60000)
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(firebaseOptions);
        }
    }


    public static ByteArrayInputStream getServiceAccountKeyStream(String serviceAccountKey) {
        String decodedString = StringUtil.decodeBase64(serviceAccountKey);
        return new ByteArrayInputStream(decodedString.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${config.api.firebaseServiceAccountKey}")
    private String serviceAccountKey;
}