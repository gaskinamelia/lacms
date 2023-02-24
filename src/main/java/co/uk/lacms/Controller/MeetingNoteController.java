package co.uk.lacms.Controller;

import co.uk.lacms.Entity.Comment;
import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Service.MeetingNoteService;
import co.uk.lacms.Service.PaginationService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MeetingNoteController {

    @Autowired
    UserService userService;

    @Autowired
    MeetingNoteService meetingNoteService;

    @Autowired
    PaginationService paginationService;

    private String idTokenLoggedInUser;

    public MeetingNoteController(UserService userService, MeetingNoteService meetingNoteService, PaginationService paginationService) {
        this.userService = userService;
        this.meetingNoteService = meetingNoteService;
        this.paginationService = paginationService;
    }

    @GetMapping("/addMeetingNote/{lacUid}")
    public ModelAndView addMeetingNote(@PathVariable("lacUid") String lacUid, Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);
        User lacUser = userService.getUserByUid(lacUid);

        model.addAttribute("meetingNote", new MeetingNote());
        model.addAttribute("user", user);
        model.addAttribute("lacUser", lacUser);

        return new ModelAndView("/meetingNote");
    }

    @PostMapping("/addMeetingNote")
    public ModelAndView submitMeetingNote(@Valid @ModelAttribute("meetingNote") MeetingNote meetingNote,
                                          BindingResult result,
                                          Model model) {

        if(result.hasErrors()){

            User user = userService.getUserByToken(userService.getLoggedInToken());
            User lacUser = userService.getUserByUid(meetingNote.getCreatedForUserUid());

            model.addAttribute("meetingNote", meetingNote);
            model.addAttribute("user", user);
            model.addAttribute("lacUser", lacUser);

            return new ModelAndView("/meetingNote");
        }

        meetingNoteService.saveMeetingNote(meetingNote);

        return new ModelAndView("redirect:/sw/dashboard");
    }

    @GetMapping("/viewMeetingNotes/{lacUid}")
    public ModelAndView viewAllMeetingNotes(@RequestParam("page") Optional<Integer> page,
                                            @RequestParam("size") Optional<Integer> size,
                                            @PathVariable("lacUid") String lacUid,
                                            Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);
        User lacUser = userService.getUserByUid(lacUid);

        ArrayList<MeetingNote> meetingNotes = meetingNoteService.getAllMeetingNotesForUser(lacUid, user.getUid());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2); //TODO: change later to 10

        Page<MeetingNote> meetingNotePage = paginationService.paginateMeetingNotes(meetingNotes, PageRequest.of(currentPage - 1, pageSize));

        int totalPages = meetingNotePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("meetingNotePage", meetingNotePage);
        model.addAttribute("user", user);
        model.addAttribute("lacUser", lacUser);

        return new ModelAndView("viewMeetingNotesDashboard");
    }

    @GetMapping("/viewMeetingNote/{lacUid}/{meetingNoteId}")
    public ModelAndView viewMeetingNote(@PathVariable("lacUid") String lacUid,
                                       @PathVariable("meetingNoteId") String meetingNoteId,
                                       Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);
        User lacUser = userService.getUserByUid(lacUid);

        MeetingNote meetingNote = meetingNoteService.getMeetingNoteForId(meetingNoteId);
        Comment recentComment = meetingNoteService.getMostRecentCommentForMeetingId(meetingNoteId);

        model.addAttribute("meetingNote", meetingNote);
        model.addAttribute("viewing", true);
        model.addAttribute("user", user);
        model.addAttribute("lacUser", lacUser);
        model.addAttribute("recentComment", meetingNoteService.getMostRecentCommentForMeetingId(meetingNoteId));

        if(recentComment != null) {
            User commentUser = userService.getUserByUid(recentComment.getCreatedByUserUid());
            model.addAttribute("recentCommenter", commentUser);
        }

        if(user.getUserType().equals(UserType.SW)) {
            model.addAttribute("canEditDelete", true);
            model.addAttribute("isLacUser", false);
        } else if(user.getUserType().equals(UserType.LAC)) {
            model.addAttribute("isLacUser", true);
        }

        model.addAttribute("canComment", true);
        model.addAttribute("comment", new Comment());

        return new ModelAndView("viewMeetingNote");
    }

    @PostMapping("/deleteMeetingNote/{lacUid}/{meetingNoteId}")
    public ModelAndView deleteMeetingNote(@PathVariable("lacUid") String lacUid,
                                          @PathVariable("meetingNoteId") String meetingNoteId,
                                          Model model) {
        meetingNoteService.deleteMeetingNote(meetingNoteId);

        return new ModelAndView("redirect:/viewMeetingNotes/" + lacUid + "?successDelete");
    }

    @GetMapping("/updateMeetingNote/{lacUid}/{meetingNoteId}")
    public ModelAndView updateMeetingNote(@PathVariable("lacUid") String lacUid,
                                        @PathVariable("meetingNoteId") String meetingNoteId,
                                        Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);
        User lacUser = userService.getUserByUid(lacUid);

        MeetingNote meetingNote = meetingNoteService.getMeetingNoteForId(meetingNoteId);

        model.addAttribute("meetingNote", meetingNote);
        model.addAttribute("updating", true);
        model.addAttribute("user", user);
        model.addAttribute("lacUser", lacUser);

        return new ModelAndView("viewMeetingNote");
    }


    @PostMapping("/updateMeetingNote")
    public ModelAndView updateMeetingNote(@Valid @ModelAttribute("meetingNote") MeetingNote meetingNote,
                                          BindingResult result,
                                          Model model) {
        if(result.hasErrors()){
            idTokenLoggedInUser = userService.getLoggedInToken();

            User user = userService.getUserByToken(idTokenLoggedInUser);
            User lacUser = userService.getUserByUid(meetingNote.getCreatedForUserUid());

            model.addAttribute("meetingNote", meetingNote);
            model.addAttribute("updating", true);
            model.addAttribute("user", user);
            model.addAttribute("lacUser", lacUser);
            return new ModelAndView("viewMeetingNote");
        }

        meetingNoteService.updateMeetingNote(meetingNote);

        return new ModelAndView("redirect:/sw/dashboard");
    }

    @PostMapping("/addComment/{lacUid}/{meetingNoteId}")
    public ModelAndView addComment(@PathVariable("meetingNoteId") String meetingNoteId,
                                   @PathVariable("lacUid") String lacUid,
                                   @Valid @ModelAttribute("comment") Comment comment,
                                   BindingResult result,
                                   Model model) {

        if(result.hasErrors()){

            idTokenLoggedInUser = userService.getLoggedInToken();

            User user = userService.getUserByToken(idTokenLoggedInUser);
            User lacUser = userService.getUserByUid(lacUid);

            MeetingNote meetingNote = meetingNoteService.getMeetingNoteForId(meetingNoteId);
            Comment recentComment = meetingNoteService.getMostRecentCommentForMeetingId(meetingNoteId);

            model.addAttribute("meetingNote", meetingNote);
            model.addAttribute("viewing", true);
            model.addAttribute("user", user);
            model.addAttribute("lacUser", lacUser);
            model.addAttribute("recentComment", meetingNoteService.getMostRecentCommentForMeetingId(meetingNoteId));

            if(recentComment != null) {
                User commentUser = userService.getUserByUid(recentComment.getCreatedByUserUid());
                model.addAttribute("recentCommenter", commentUser);
            }

            if(user.getUserType().equals(UserType.SW)) {
                model.addAttribute("canEditDelete", true);
                model.addAttribute("isLacUser", false);
            } else if(user.getUserType().equals(UserType.LAC)) {
                model.addAttribute("isLacUser", true);
            }

            model.addAttribute("canComment", true);
            model.addAttribute("errors", true);
            model.addAttribute("comment", comment);

            return new ModelAndView("viewMeetingNote");
        }

        comment.setCreatedDateTime(LocalDateTime.now());

        meetingNoteService.addComments(comment);

        return new ModelAndView("redirect:/viewMeetingNote/" + lacUid + "/" + meetingNoteId);
    }

    @PostMapping("/archiveNote/{meetingNoteId}")
    public ModelAndView archiveNote(@PathVariable("meetingNoteId") String meetingNoteId,
                                    Model model) {
        meetingNoteService.archiveNote(meetingNoteId);
        return new ModelAndView("redirect:/lac/dashboard" );

    }

    @GetMapping("/viewAllComments/{meetingNoteId}")
    public ModelAndView viewAllComments(@RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size,
                                        @PathVariable("meetingNoteId") String meetingNoteId,
                                        Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);

        MeetingNote meetingNote = meetingNoteService.getMeetingNoteForId(meetingNoteId);

        ArrayList<Comment> comments = meetingNoteService.getAllCommentsForMeetingNote(meetingNoteId);

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2); //TODO: change later to 10

        Page<Comment> commentPage = paginationService.paginationComments(comments, PageRequest.of(currentPage - 1, pageSize));

        int totalPages = commentPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("commentPage", commentPage);
        model.addAttribute("user", user);
        model.addAttribute("meetingNote", meetingNote);

        return new ModelAndView("viewAllComments");
    }


}
