package co.uk.lacms.Service;

import co.uk.lacms.Entity.LacUser;
import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class SearchService {

    @Autowired
    private final Firestore firestore;

    @Autowired
    private final UserService userService;

    @Autowired
    private final MeetingNoteService meetingNoteService;

    public SearchService(Firestore firestore, UserService userService, MeetingNoteService meetingNoteService) {
        this.firestore = firestore;
        this.userService = userService;
        this.meetingNoteService = meetingNoteService;
    }

    /**
     * Search firestore list of users that are lac users where the email of the user contains the query string.
     * If there isn't a match with the query string then return empty list.
     * @param queryString The query string to match with some part of the email in list of users
     * @return users The list of lac users that match the query string or empty list.
     */
    public ArrayList<LacUser> searchLacUser(String queryString) {
        ArrayList<LacUser> result = new ArrayList<>();

        CollectionReference users = firestore.collection("Users");

        Query query = users
                .whereEqualTo("userType", UserType.LAC.getDisplayName())
                .whereGreaterThanOrEqualTo("email", queryString)
                .whereLessThanOrEqualTo("email",  queryString + '~');

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
     * Search firestore list of users that are lac users where the email of the user contains
     * the query string and the lacuser is linked to the social worker user.
     * If there isn't a match with the query string then return empty list.
     * @param queryString The query string to match with some part of the email in list of users
     * @param socialWorkerUser The social worker user that is currently logged in
     * @return users The list of lac users that match the query string or empty list.
     */
    public ArrayList<LacUser> searchLacUserForSocialWorkerUser(String queryString, User socialWorkerUser) {
        ArrayList<LacUser> result = new ArrayList<>();

        CollectionReference users = firestore.collection("Users");

        Query query = users
                .whereEqualTo("userType", UserType.LAC.getDisplayName())
                .whereEqualTo("social_worker_uid", socialWorkerUser.getUid())
                .whereGreaterThanOrEqualTo("email", queryString)
                .whereLessThanOrEqualTo("email",  queryString + '~');

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {

            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {

                LacUser user = new LacUser(document.getId(), document.get("email").toString(), document.get("firstName").toString(), document.get("lastName").toString(), UserType.fromDisplayName(document.get("userType").toString()), document.get("token").toString(), socialWorkerUser);

                result.add(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Search firestore list of meeting notes where the title contains query string.
     * If there isn't a match with the query string then return empty list.
     * @param lacUid The lac users uid that is linked to the meeting note
     * @param socialWorkerUid The social worker user uid that is linked to the meeting note
     * @param queryString The query string to match with some part of the title for the meeting note
     * @param viewArchived Whether to include archived notes in the result or not
     * @return meetingNote The list of meeting note that match the query string or empty list.
     */
    public ArrayList<MeetingNote> searchMeetingNotes(String lacUid, String socialWorkerUid, String queryString, boolean viewArchived) {
        ArrayList<MeetingNote> result = new ArrayList<>();

        CollectionReference meetingNotes = firestore.collection("MeetingNotes");

        Query query = meetingNotes
                .whereEqualTo("created_by_social_worker", socialWorkerUid)
                .whereEqualTo("lac_user", lacUid)
                .whereEqualTo("deleted", false)
                .whereIn("archived", !viewArchived ? Collections.singletonList(false) : List.of(false, true))
                .orderBy("title", Query.Direction.ASCENDING)
                .whereGreaterThanOrEqualTo("title", queryString)
                .whereLessThanOrEqualTo("title", queryString + '~')
                .orderBy("created_date", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                LocalDateTime updatedDate = null;
                String updatedBy = null;

                if(document.get("updated_by") != null) {
                    updatedBy = document.get("updated_by").toString();
                    updatedDate = meetingNoteService.convertStringDateToLocalDateTime(document.get("updated_date").toString());
                }

                MeetingNote meetingNote = new MeetingNote(
                        document.getId(),
                        document.get("created_by_social_worker").toString(),
                        document.get("lac_user").toString(),
                        document.get("title").toString(),
                        document.get("notes").toString(),
                        meetingNoteService.convertStringDateToLocalDateTime(document.get("created_date").toString()),
                        updatedBy,
                        updatedDate,
                        document.getBoolean("archived"));

                result.add(meetingNote);
            }
        } catch (InterruptedException | ExecutionException | ParseException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
