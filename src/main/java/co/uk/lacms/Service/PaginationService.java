package co.uk.lacms.Service;

import co.uk.lacms.Entity.Comment;
import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PaginationService {

    public Page<User> paginateUsers(ArrayList<User> users, Pageable pageable) {

        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int startItem = currentPage * pageSize;
        List<User> list;

        if (users.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            list = users.subList(startItem, toIndex);
        }

        Page<User> page = new PageImpl<User>(list, PageRequest.of(currentPage, pageSize), users.size());

        return page;
    }

    public Page<MeetingNote> paginateMeetingNotes(ArrayList<MeetingNote> meetingNotes, Pageable pageable) {

        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int startItem = currentPage * pageSize;
        List<MeetingNote> list;

        if (meetingNotes.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, meetingNotes.size());
            list = meetingNotes.subList(startItem, toIndex);
        }

        Page<MeetingNote> page = new PageImpl<MeetingNote>(list, PageRequest.of(currentPage, pageSize), meetingNotes.size());

        return page;
    }

    public Page<Comment> paginationComments(ArrayList<Comment> comments, Pageable pageable) {

        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int startItem = currentPage * pageSize;
        List<Comment> list;

        if (comments.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, comments.size());
            list = comments.subList(startItem, toIndex);
        }

        Page<Comment> page = new PageImpl<Comment>(list, PageRequest.of(currentPage, pageSize), comments.size());

        return page;
    }
}
