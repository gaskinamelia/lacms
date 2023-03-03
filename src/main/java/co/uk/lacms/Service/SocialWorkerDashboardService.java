package co.uk.lacms.Service;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
public class SocialWorkerDashboardService {

    @Autowired
    private final FirebaseAuthManager firebaseAuthManager;
    @Autowired
    private final FirebaseAuth firebaseAuth;
    @Autowired
    private final Firestore firestore;

    public SocialWorkerDashboardService(FirebaseAuthManager firebaseAuthManager, FirebaseAuth firebaseAuth, Firestore firestore) {
        this.firebaseAuthManager = firebaseAuthManager;
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
    }

    /**
     * Retrieve a list of looked after child users that are assigned
     * to the current logged in social worker user
     * @param user The user that is currently logged in
     * @return list of user objects assigned to social worker
     */
    public ArrayList<User> getAllLacForLoggedInSocialWorker(User user) {
        ArrayList<User> result = new ArrayList<>();

        CollectionReference users = firestore.collection("Users");

        Query query = users
                .whereEqualTo("userType", UserType.LAC.getDisplayName())
                .whereEqualTo("social_worker_uid", user.getUid());

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                User lacUser = new User(document.getId(), document.get("email").toString(), document.get("firstName").toString(), document.get("lastName").toString(), UserType.fromDisplayName(document.get("userType").toString()), document.get("token").toString());

                result.add(lacUser);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


}
