package co.uk.lacms.Service;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private final FirebaseAuthManager firebaseAuthManager;
    @Autowired
    private final FirebaseAuth firebaseAuth;
    @Autowired
    private final Firestore firestore;

    public String idTokenLoggedInUser;

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
     */
    public String authenticateUser(String email, String hashedPassword) throws FirebaseAuthException{
        return firebaseAuthManager.auth(email, hashedPassword);
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

        String userUid = getUidForUserByToken(token);

        DocumentReference docRef = firestore.collection("Users").document(getUidForUserByToken(token));
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("userType", user.getUserType().getDisplayName());

        if(user.getUserType().equals(UserType.LAC)) {
            data.put("social_worker_uid", null);
        } else if(user.getUserType().equals(UserType.SW)) {
            data.put("social_worker_manager_uid",  null);
        }

        data.put("token", token);

        docRef.set(data);

        return token;
    }

    /**
     * Gets the user by their email using firebaseAuth
     * @param email The email associated with the user we want to find
     * @return The user record object of the user we want or null if no user exists
     */
    public UserRecord getUserRecordByEmail(String email) {
        UserRecord user = null;

        try {
            user = firebaseAuth.getUserByEmail(email);
        } catch (FirebaseAuthException e) {
            return null;
        }

        return user;
    }

    /**
     * Get user object by token from firebase rest api, if no document
     * exists in firestore return null, else return user object.
     * @param token The token associated with the user object
     * @return user object or null if no user exists
     */
    public User getUserByToken(String token){
        DocumentReference docRef = firestore.collection("Users").document(getUidForUserByToken(token));
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;

        User user = null;
        try {
            document = future.get();
            String email = document.get("email").toString();
            String firstName = document.get("firstName").toString();
            String lastName = document.get("lastName").toString();
            UserType userType = UserType.fromDisplayName(document.get("userType").toString());

            user = new User(getUidForUserByToken(token), email, firstName, lastName, userType, token);

        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

        return user;
    }

    public UserType getUserTypeByToken(String token) {
        return getUserByToken(token).getUserType();
    }

    public void setLoggedInToken(String token) {
        idTokenLoggedInUser = token;
    }

    public String getLoggedInToken() {
        return idTokenLoggedInUser;
    }

    public User getUserByUid(String uid) {
        DocumentReference docRef = firestore.collection("Users").document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;

        User user = null;
        try {
            document = future.get();
            String email = document.get("email").toString();
            String firstName = document.get("firstName").toString();
            String lastName = document.get("lastName").toString();
            UserType userType = UserType.fromDisplayName(document.get("userType").toString());
            String token = document.get("token").toString();

            user = new User(uid, email, firstName, lastName, userType, token);

        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

        return user;
    }

    /**
     * Check if user has the authority to access
     * @param user The user to authorise
     * @param userTypeAllowed The userType that is authorised
     */
    public void authoriseUser(User user, List<UserType> userTypeAllowed) {
        boolean notAuthorised = userTypeAllowed.stream().noneMatch(userType -> user.getUserType().equals(userType));

        if(notAuthorised) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get the social worker user for lac user
     * @param lacUid The lac user uid
     * @return User the social worker user for the lac user
     */
    public Optional<User> getSocialWorkerUserWithLacUid(String lacUid) {
        Optional<String> social_worker_uid = getSocialWorkerUidForLacUid(lacUid);

        if(social_worker_uid.isEmpty()) {
            return Optional.empty();
        }

        DocumentReference docRef = firestore.collection("Users").document(social_worker_uid.get());
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;

        User result = null;
        try {
            document = future.get();
            String email = document.get("email").toString();
            String firstName = document.get("firstName").toString();
            String lastName = document.get("lastName").toString();
            String token = document.get("token").toString();
            UserType userType = UserType.fromDisplayName(document.get("userType").toString());

            result = new User(social_worker_uid.get(), email, firstName, lastName, userType, token);

        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    /**
     * Get the social worker user uid for lac user
     * @param lacUid The lac user uid
     * @return User uid the social worker user for the lac user
     */
    private Optional<String> getSocialWorkerUidForLacUid(String lacUid) {

        DocumentReference docRef = firestore.collection("Users").document(lacUid);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;

        String result = null;
        try {
            document = future.get();

            result = Objects.requireNonNull(document.get("social_worker_uid")).toString();

        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

}
