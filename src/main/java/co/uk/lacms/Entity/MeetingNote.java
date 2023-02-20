package co.uk.lacms.Entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MeetingNote {

    private String id;
    private String createdByUserUid;
    private String createdForUserUid;
    private String title;
    private String notes;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdDateTime;
    private String updatedByUserUid;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime updatedDateTime;
    private boolean archived;

    public MeetingNote() {}

    public MeetingNote(String id, String createdByUserUid, String createdForUserUid, String title, String notes, LocalDateTime createdDateTime, String updatedByUserUid, LocalDateTime updatedDateTime, boolean archived) {
        this.id = id;
        this.createdByUserUid = createdByUserUid;
        this.createdForUserUid = createdForUserUid;
        this.title = title;
        this.notes = notes;
        this.createdDateTime = createdDateTime;
        this.updatedByUserUid = updatedByUserUid;
        this.updatedDateTime = updatedDateTime;
        this.archived = archived;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedByUserUid() {
        return createdByUserUid;
    }

    public void setCreatedByUserUid(String createdByUserUid) {
        this.createdByUserUid = createdByUserUid;
    }

    public String getCreatedForUserUid() {
        return createdForUserUid;
    }

    public void setCreatedForUserUid(String createdForUserUid) {
        this.createdForUserUid = createdForUserUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getUpdatedByUserUid() {
        return updatedByUserUid;
    }

    public void setUpdatedByUserUid(String updatedByUserUid) {
        this.updatedByUserUid = updatedByUserUid;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getDisplayCreatedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm");
        return createdDateTime.format(formatter);
    }
}
