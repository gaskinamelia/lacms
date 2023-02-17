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
public class SocialWorkerManagerDashboardService {

    @Autowired
    private FirebaseAuthManager firebaseAuthManager;
    @Autowired
    private FirebaseAuth firebaseAuth;
    @Autowired
    private Firestore firestore;

    public SocialWorkerManagerDashboardService(FirebaseAuthManager firebaseAuthManager, FirebaseAuth firebaseAuth, Firestore firestore) {
        this.firebaseAuthManager = firebaseAuthManager;
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
    }

    /**
     * Retrieve a list of looked after children that have
     * unassigned social worker field.
     * @return list of user objects returned from query
     */
    public ArrayList<User> getAllLACWithUnassignedSocialWorker() {

        ArrayList<User> result = new ArrayList<>();

        CollectionReference users = firestore.collection("Users");

        Query query = users
                .whereEqualTo("userType", UserType.LAC.getDisplayName())
                .whereEqualTo("social_worker_uid", null);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                User user = new User(document.getId(), document.get("email").toString(), document.get("firstName").toString(), document.get("lastName").toString(), UserType.fromDisplayName(document.get("userType").toString()), document.get("token").toString());

                result.add(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Return a list of all social worker users from the user collection
     * @param loggedInUserUid Current logged in user uid
     * @return a list of user objects returned from the query
     */
    public ArrayList<User> getAllSocialWorkerUsersForManagerUser(String loggedInUserUid) {
        ArrayList<User> result = new ArrayList<>();

        CollectionReference users = firestore.collection("Users");

        Query query = users
                .whereEqualTo("userType", UserType.SW.getDisplayName())
                .whereEqualTo("social_worker_manager_uid", loggedInUserUid);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                User user = new User(document.getId(), document.get("email").toString(), document.get("firstName").toString(), document.get("lastName").toString(), UserType.fromDisplayName(document.get("userType").toString()), document.get("token").toString());

                result.add(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Update lac user with an assigned social worker
     * @param lacUid The lac user uid to update
     * @param swUid The uid of the social worker to assign to the lac
     */
    public void assignSocialWorkerToLac(String lacUid, String swUid) {
        DocumentReference docRef = firestore.collection("Users").document(lacUid);

        docRef.update("social_worker_uid", swUid);

    }
}
