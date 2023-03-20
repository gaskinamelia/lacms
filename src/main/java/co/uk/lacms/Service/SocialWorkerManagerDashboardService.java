package co.uk.lacms.Service;

import co.uk.lacms.Entity.LacUser;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.FilterManagerDashboardForm;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class SocialWorkerManagerDashboardService {
    @Autowired
    private final Firestore firestore;

    @Autowired
    private final UserService userService;

    public SocialWorkerManagerDashboardService(Firestore firestore, UserService userService) {
        this.firestore = firestore;
        this.userService = userService;
    }

    /**
     * Retrieve a list of looked after children users based
     * on the filter form for dashboard
     * @return list of user objects returned from query
     */
    public ArrayList<LacUser> getFilteredLacUsers(FilterManagerDashboardForm form) {

        ArrayList<LacUser> result = new ArrayList<>();

        CollectionReference users = firestore.collection("Users");

        Query query = users
                .whereEqualTo("userType", UserType.LAC.getDisplayName());

        if(form.isUnassignedLacs() && !form.isAssignedLacs()) {
            query = query.whereEqualTo("social_worker_uid", null);
        } else if(!form.isUnassignedLacs() && form.isAssignedLacs()) {
            query = query.whereNotEqualTo("social_worker_uid", null);
        }

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {

            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {

                Optional<User> socialWorkerUser = userService.getSocialWorkerUserWithLacUid(document.getId());

                LacUser user = new LacUser(document.getId(), document.get("email").toString(), document.get("firstName").toString(), document.get("lastName").toString(), UserType.fromDisplayName(document.get("userType").toString()), document.get("token").toString(), socialWorkerUser.orElse(null));

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
