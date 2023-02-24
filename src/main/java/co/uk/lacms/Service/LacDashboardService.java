package co.uk.lacms.Service;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class LacDashboardService {

    @Autowired
    private final FirebaseAuthManager firebaseAuthManager;
    @Autowired
    private final FirebaseAuth firebaseAuth;
    @Autowired
    private final Firestore firestore;

    public LacDashboardService(FirebaseAuthManager firebaseAuthManager, FirebaseAuth firebaseAuth, Firestore firestore) {
        this.firebaseAuthManager = firebaseAuthManager;
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
    }

    /**
     * Retrieve social worker user for current logged in LAC user
     * @param user The user that is currently logged in
     * @return social worker user The social worker assigned to the LAC
     */
    public User getSocialWorkerForLoggedInLAC(User user) {

        String social_worker_uid = getSwUidForLacUser(user);

        DocumentReference docRef = firestore.collection("Users").document(social_worker_uid);
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

            result = new User(social_worker_uid, email, firstName, lastName, userType, token);

        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

        return result;
    }

    /**
     * Get the social worker uid from the user document
     * @param user The user we want the social worker uid from
     * @return social worker uid
     */
    private String getSwUidForLacUser(User user) {
        DocumentReference docRef = firestore.collection("Users").document(user.getUid());
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;

        String result = null;
        try {
            document = future.get();

            result = document.get("social_worker_uid").toString();

        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
        return result;
    }

}
