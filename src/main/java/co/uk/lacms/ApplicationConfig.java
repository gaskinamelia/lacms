package co.uk.lacms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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
}
