package co.uk.lacms.Service;

import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to interface with the Firebase Auth REST API.
 */
@Service
public class FirebaseAuthManager {
    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:";
    private static final String OPERATION_AUTH = "signInWithPassword";

    private static final String OPERATION_SIGN_UP = "signUp";
    private static final String OPERATION_ACCOUNT_INFO = "lookup";
    private static final String OPERATION_SEND_PASSWORD_RESET = "getOobConfirmationCode";

    private final String firebaseKey;
    private static FirebaseAuthManager instance = null;

    protected FirebaseAuthManager() {
        firebaseKey = "AIzaSyBS-9Qdxf9ZscTLRABpXu7gseYgiouzDb8";
    }

    public static FirebaseAuthManager getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthManager();
        }
        return instance;
    }

    /**
     * Exchange an email and password with the Firebase Auth REST API for an ID token.
     * @param email A email or email registered with Firebase Authentication.
     * @param password The password associated with the email or email.
     * @return An ID token from Firebase.
     * @throws FirebaseAuthException
     */
    public String auth(String email, String password) throws FirebaseAuthException {
        String token;
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(BASE_URL + OPERATION_AUTH + "?key=" + firebaseKey);
            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("returnSecureToken", "true"));
            httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            HttpEntity entity = httpclient.execute(httppost).getEntity();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(entity.getContent()));
            JsonObject rootObj = root.getAsJsonObject();

            if (rootObj.get("error") != null) {
                throw new FirebaseAuthException(ErrorCode.UNKNOWN, rootObj.get("error").getAsJsonObject().get("message").getAsString(), null, null, null);
            }
            token = rootObj.get("idToken").getAsString();
        } catch (IOException e) { System.out.println(e.getMessage()); return null; }
        return token;
    }

    /**
     * Exchange an email and password with the Firebase Auth REST API for an ID token.
     * @param email A email or email to register with Firebase Authentication.
     * @param password The password associated with the email or email.
     * @return An ID token from Firebase.
     * @throws FirebaseAuthException
     */
    public String signUpWithEmailPassword(String email, String password) throws FirebaseAuthException {
        String token;
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(BASE_URL + OPERATION_SIGN_UP + "?key=" + firebaseKey);
            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("returnSecureToken", "true"));
            httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            HttpEntity entity = httpclient.execute(httppost).getEntity();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(entity.getContent()));
            JsonObject rootObj = root.getAsJsonObject();

            if (rootObj.get("error") != null) {
                throw new FirebaseAuthException(ErrorCode.UNKNOWN, rootObj.get("error").getAsJsonObject().get("message").getAsString(), null, null, null);
            }
            token = rootObj.get("idToken").getAsString();
        } catch (IOException e) { System.out.println(e.getMessage()); return null; }
        return token;
    }
    /**
     * Exchange an ID token with the Firebase Auth REST API for a User object.
     * @param token An ID token from Firebase.
     * @return A user object with the email and UID returned by Firebase.
     */
//    public User getAccountInfo(String token) {
//        try {
//            HttpClient httpclient = HttpClients.createDefault();
//            HttpPost httppost = new HttpPost(BASE_URL + OPERATION_ACCOUNT_INFO + "?key=" + firebaseKey);
//            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
//            params.add(new BasicNameValuePair("idToken", token));
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//            HttpEntity entity = httpclient.execute(httppost).getEntity();
//
//            JsonParser jp = new JsonParser();
//            JsonElement root = jp.parse(new InputStreamReader(entity.getContent()));
//            JsonObject rootObj = root.getAsJsonObject();
//
//            JsonObject userObj = rootObj.get("users").getAsJsonArray().get(0).getAsJsonObject();
//
//            return new User(userObj.get("localId").getAsString(), userObj.get("email").getAsString(), null, null, null, token);
//        } catch (IOException e) { System.out.println(e.getMessage()); return null; }
//    }

    /**
     * Exchamge an ID token with the Firebase Auth REST API for the users UID object.
     * @param token An ID token from firebase
     * @return The UID from the user
     */
    public String getUidForUserByToken(String token) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(BASE_URL + OPERATION_ACCOUNT_INFO + "?key=" + firebaseKey);
            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("idToken", token));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpEntity entity = httpclient.execute(httppost).getEntity();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(entity.getContent()));
            JsonObject rootObj = root.getAsJsonObject();

            JsonObject userObj = rootObj.get("users").getAsJsonArray().get(0).getAsJsonObject();

            return userObj.get("localId").getAsString();
        } catch (IOException e) { System.out.println(e.getMessage()); return null; }
    }

    /**
     * Send a reset password email via Firebase.
     * @param email The email address to send the reset password email to.
     */
    public void sendResetPasswordLink(String email) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(BASE_URL + OPERATION_SEND_PASSWORD_RESET + "?key=" + firebaseKey);
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("requestType", "PASSWORD_RESET"));
            params.add(new BasicNameValuePair("email", email));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpclient.execute(httppost);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Exchange a refresh token with the Firebase Auth REST API for a new ID token.
     * @param refreshToken The refresh token used to receive an ID token.
     * @return A new ID token from Firebase.
     */
    public String[] exchangeRefreshToken(String refreshToken) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("https://securetoken.googleapis.com/v1/token?key="+ firebaseKey);
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            params.add(new BasicNameValuePair("refresh_token", refreshToken));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpEntity entity = httpclient.execute(httppost).getEntity();
            if (entity != null) {
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader(entity.getContent()));
                JsonObject rootObj = root.getAsJsonObject();
                return new String[]{ rootObj.get("id_token").getAsString(), rootObj.get("refresh_token").getAsString() };
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}
