package co.uk.lacms.Service;

import co.uk.lacms.Entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private FirebaseAuthManager firebaseAuthManager;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Autowired
    private Firestore firestore;

    public UserService(FirebaseAuthManager firebaseAuthManager, FirebaseAuth firebaseAuth, Firestore firestore) {
        this.firebaseAuthManager = firebaseAuthManager;
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
    }

    /**
     * Authenticates user using email and password.
     * @param email
     * @param hashedPassword
     * @return token. The idToken for the logged in user.
     * @throws FirebaseAuthException
     */
    public String autheticateUser(String email, String hashedPassword) throws FirebaseAuthException {
        return firebaseAuthManager.auth(email, hashedPassword);
    }

    /**
     * Gets the user object by the token from firebase.
     * @param idToken The token from firebase associated to the user
     * @return The user object from firebase rest api.
     */
    public User getUserByToken(String idToken) {
        return firebaseAuthManager.getAccountInfo(idToken);
    }

    /**
     * Gets the uid for the user by the token from firebase.
     * @param idToken The token from firebase associated with the user
     * @return The Uid from the user record
     */
    private String getUidForUserByToken(String idToken) {
        return firebaseAuthManager.getUidForUserByToken(idToken);
    }

    /**
     * Registers user to the firebase authentication, as well as saving
     * the user details to the firestore collection users.
     * @param user The user that is registering
     * @return The token from registering the user
     * @throws FirebaseAuthException
     */
    public String signUpUser(User user) throws FirebaseAuthException {

        String token = firebaseAuthManager.signUpWithEmailPassword(user.getEmail(), user.getPassword());

        DocumentReference docRef = firestore.collection("Users").document(getUidForUserByToken(token));
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("userType", user.getUserType().getDisplayName());
        data.put("token", token);

        ApiFuture<WriteResult> result = docRef.set(data);

        return token;
    }

    /**
     * Gets the user by their email using firebaseAuth
     * @param email The email associated with the user we want to find
     * @return The user record object of the user we want or null if no user exists
     */
    public UserRecord getUserByEmail(String email) {
        UserRecord user = null;

        try {
            user = firebaseAuth.getUserByEmail(email);
        } catch (FirebaseAuthException e) {
            return null;
        }

        return user;
    }

}
