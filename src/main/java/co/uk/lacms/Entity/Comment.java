package co.uk.lacms.Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {

    private String commentId;

    private String comments;

    private String createdByUserUid;

    private LocalDateTime createdDateTime;

    private String meetingId;

    private String createdUserName;

    public Comment(){

    }

    public Comment(String commentId, String comments, String createdByUserUid, LocalDateTime createdDateTime, String meetingId) {
        this.commentId = commentId;
        this.comments = comments;
        this.createdByUserUid = createdByUserUid;
        this.createdDateTime = createdDateTime;
        this.meetingId = meetingId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedByUserUid() {
        return createdByUserUid;
    }

    public void setCreatedByUserUid(String createdByUserUid) {
        this.createdByUserUid = createdByUserUid;
    }

    public String getCreatedUserName() {
       return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getDisplayCreatedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm");
        return createdDateTime.format(formatter);
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
