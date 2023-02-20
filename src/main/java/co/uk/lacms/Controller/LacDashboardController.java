package co.uk.lacms.Controller;

import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Service.LacDashboardService;
import co.uk.lacms.Service.MeetingNoteService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class LacDashboardController {

    @Autowired
    UserService userService;

    @Autowired
    LacDashboardService lacDashboardService;

    @Autowired
    MeetingNoteService meetingNoteService;

    private String idTokenLoggedInUser;

    public LacDashboardController(UserService userService, LacDashboardService lacDashboardService, MeetingNoteService meetingNoteService) {
        this.userService = userService;
        this.lacDashboardService = lacDashboardService;
        this.meetingNoteService = meetingNoteService;
    }

    @GetMapping("/lac/dashboard")
    public ModelAndView lacDashboard(Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);

            User socialWorkerUser = lacDashboardService.getSocialWorkerForLoggedInLAC(user);

            ArrayList<MeetingNote> meetingNotes = meetingNoteService.getAllMeetingNotesForUser(user.getUid(), socialWorkerUser.getUid());

            model.addAttribute("user", user);
            model.addAttribute("socialWorkerUser", socialWorkerUser);
            model.addAttribute("meetingNotes", meetingNotes);

            return new ModelAndView("lacDashboard");
        }
        return new ModelAndView("redirect:/login");
    }
}
