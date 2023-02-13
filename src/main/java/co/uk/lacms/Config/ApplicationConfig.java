package co.uk.lacms.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Component
public class ApplicationConfig {
    @Value("${firebase.dbBaseUrl}")
    private String bdBaseUrl;
    @Value("${firebase.authFileLocation}")
    private String authFileLocation;

    public String getBdBaseUrl() {
        return bdBaseUrl;
    }

    public String getAuthFileLocation() {
        return authFileLocation;
    }
    @Bean
    public FirebaseApp createFirebaseApp() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(getAuthFileLocation());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(getBdBaseUrl())

                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    @DependsOn(value="createFirebaseApp")
    public FirebaseAuth createFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Bean
    @DependsOn(value="createFirebaseApp")
    public FirebaseDatabase createFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Bean
    @DependsOn(value="createFirebaseApp")
    public Firestore createFirestore() {
        return FirestoreClient.getFirestore();
    }

}
