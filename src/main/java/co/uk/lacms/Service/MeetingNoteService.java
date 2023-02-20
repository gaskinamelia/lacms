package co.uk.lacms.Service;

import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class MeetingNoteService {
    @Autowired
    private Firestore firestore;

    public MeetingNoteService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Add a meeting note document to the firestore database
     * @param meetingNote The meeting note we want to add to the database
     */
    public void saveMeetingNote(MeetingNote meetingNote) {
        DocumentReference documentReference = firestore.collection("MeetingNotes").document();

        Map<String, Object> data = new HashMap<>();
        data.put("created_by_social_worker", meetingNote.getCreatedByUserUid());

        //Changed to date type as firestore only accepts Date for Timestamp
        Instant instant = meetingNote.getCreatedDateTime().toInstant(ZoneOffset.UTC);
        Date createdDate = Date.from(instant);

        data.put("created_date", createdDate);
        data.put("lac_user", meetingNote.getCreatedForUserUid());
        data.put("title", meetingNote.getTitle());
        data.put("notes", meetingNote.getNotes());
        data.put("updated_by", meetingNote.getUpdatedByUserUid());
        data.put("updated_date", meetingNote.getUpdatedDateTime());
        data.put("archived", meetingNote.isArchived());

        documentReference.set(data);
    }

    /**
     * Get all meeting notes for lac user that aren't archived
     * @param lacUid The looked after child uid
     * @param socialWorkerUid The social worker uid
     * @return list of meeting notes
     */
    public ArrayList<MeetingNote> getAllMeetingNotesForUser(String lacUid, String socialWorkerUid) {
        ArrayList<MeetingNote> result = new ArrayList<>();

        CollectionReference meetingNotes = firestore.collection("MeetingNotes");

        Query query = meetingNotes
                .whereEqualTo("created_by_social_worker", socialWorkerUid)
                .whereEqualTo("lac_user", lacUid)
                .whereEqualTo("archived", false)
                .orderBy("created_date", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for(DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                LocalDateTime updatedDate = null;
                String updatedBy = null;

                if(document.get("updated_by") != null) {
                    updatedBy = document.get("updated_by").toString();
                    updatedDate = convertStringDateToLocalDateTime(document.get("updated_date").toString());
                }

                MeetingNote meetingNote = new MeetingNote(
                        document.getId(),
                        document.get("created_by_social_worker").toString(),
                        document.get("lac_user").toString(),
                        document.get("title").toString(),
                        document.get("notes").toString(),
                        convertStringDateToLocalDateTime(document.get("created_date").toString()),
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

    /**
     * Get the meeting note document from the meeting note id
     * @param meetingNoteId The meeting id we want to retrieve
     * @return meeting note
     */
    public MeetingNote getMeetingNoteForId(String meetingNoteId) {
        DocumentReference docRef = firestore.collection("MeetingNotes").document(meetingNoteId);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;

        MeetingNote meetingNote = null;
        try {
            document = future.get();

            String updatedByUserUid = null;
            LocalDateTime updatedDateTime = null;
            if(document.get("updatedByUserUid") != null) {
                updatedByUserUid = document.get("updatedByUserUid").toString();
                updatedDateTime = convertStringDateToLocalDateTime(document.get("updated_date").toString());
            }
            String createdByUserUid =  document.get("created_by_social_worker").toString();
            String createdForUserUid = document.get("lac_user").toString();
            String title = document.get("title").toString();
            String notes = document.get("notes").toString();
            LocalDateTime createdDateTime = convertStringDateToLocalDateTime(document.get("created_date").toString());
            boolean archived = document.getBoolean("archived");

            meetingNote = new MeetingNote(meetingNoteId, createdByUserUid, createdForUserUid, title, notes, createdDateTime, updatedByUserUid, updatedDateTime,archived);

        } catch (InterruptedException | ExecutionException e) {
            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return meetingNote;
    }

    private LocalDateTime convertStringDateToLocalDateTime(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm", Locale.ENGLISH);

        Date createdDate = formatter.parse(date);

        Instant instant = Instant.ofEpochMilli(createdDate.getTime());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public void deleteMeetingNote(String meetingNoteId) {
        firestore.collection("MeetingNotes").document(meetingNoteId).delete();
    }

    public void updateMeetingNote(MeetingNote meetingNote) {
        DocumentReference documentReference = firestore.collection("MeetingNotes").document(meetingNote.getId());

        documentReference.update("created_by_social_worker", meetingNote.getCreatedByUserUid());

        //Changed to date type as firestore only accepts Date for Timestamp
        Instant instantCreated = meetingNote.getCreatedDateTime().toInstant(ZoneOffset.UTC);
        Date createdDate = Date.from(instantCreated);

        Instant instantUpdated = Instant.now();
        Date updatedDate = Date.from(instantUpdated);


        documentReference.update("created_date", createdDate);
        documentReference.update("lac_user", meetingNote.getCreatedForUserUid());
        documentReference.update("title", meetingNote.getTitle());
        documentReference.update("notes", meetingNote.getNotes());
        documentReference.update("updated_by", meetingNote.getUpdatedByUserUid());
        documentReference.update("updated_date", updatedDate);
        documentReference.update("archived", meetingNote.isArchived());
    }
}
