package co.uk.lacms.Controller;

import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Service.MeetingNoteService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class MeetingNoteController {

    @Autowired
    UserService userService;

    @Autowired
    MeetingNoteService meetingNoteService;

    private String idTokenLoggedInUser;

    public MeetingNoteController(UserService userService, MeetingNoteService meetingNoteService) {
        this.userService = userService;
        this.meetingNoteService = meetingNoteService;
    }

    @GetMapping("/addMeetingNote/{lacUid}")
    public ModelAndView addMeetingNote(@PathVariable("lacUid") String lacUid, Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);
        User lacUser = userService.getUserByUid(lacUid);

        model.addAttribute("meetingNote", new MeetingNote());
        model.addAttribute("user", user);
        model.addAttribute("lacUser", lacUser);

        return new ModelAndView("meetingNote");
    }

    @PostMapping("/addMeetingNote")
    public ModelAndView submitMeetingNote(@ModelAttribute("meetingNote") MeetingNote meetingNote, Model model) {
        meetingNoteService.saveMeetingNote(meetingNote);

        return new ModelAndView("redirect:/sw/dashboard");
    }

    @GetMapping("/viewMeetingNotes/{lacUid}")
    public ModelAndView viewAllMeetingNotes(@PathVariable("lacUid") String lacUid, Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        User user = userService.getUserByToken(idTokenLoggedInUser);
        User lacUser = userService.getUserByUid(lacUid);

        ArrayList<MeetingNote> meetingNotes = meetingNoteService.getAllMeetingNotesForUser(lacUid, user.getUid());

        model.addAttribute("meetingNotes", meetingNotes);
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


        model.addAttribute("meetingNote", meetingNote);
        model.addAttribute("viewing", true);
        model.addAttribute("user", user);
        model.addAttribute("lacUser", lacUser);
        if(user.getUserType().equals(UserType.SW)) {
            model.addAttribute("canEditDelete", true);
        }

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
    public ModelAndView updateMeetingNote(@ModelAttribute("meetingNote") MeetingNote meetingNote, Model model) {
        meetingNoteService.updateMeetingNote(meetingNote);

        return new ModelAndView("redirect:/sw/dashboard");
    }

}
