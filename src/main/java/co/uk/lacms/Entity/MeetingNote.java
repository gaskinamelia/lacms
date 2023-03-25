package co.uk.lacms.Entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MeetingNote {

    private String id;
    private String createdByUserUid;
    private String createdForUserUid;
    @NotEmpty(message = "Please enter a title for the meeting note.")
    private String title;

    @NotEmpty(message = "Please enter the meeting notes.")
    @Size(min = 10, message = "Please enter more meeting notes.")
    private String notes;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Please enter the date and time the meeting took place.")
    private LocalDateTime createdDateTime;
    private String updatedByUserUid;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime updatedDateTime;
    private boolean archived;

    public MeetingNote() {
    }

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
