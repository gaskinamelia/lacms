package co.uk.lacms.Controller;

import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Service.LacDashboardService;
import co.uk.lacms.Service.MeetingNoteService;
import co.uk.lacms.Service.PaginationService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class LacDashboardController {

    @Autowired
    UserService userService;

    @Autowired
    LacDashboardService lacDashboardService;

    @Autowired
    MeetingNoteService meetingNoteService;

    @Autowired
    PaginationService paginationService;

    private String idTokenLoggedInUser;

    public LacDashboardController(UserService userService, LacDashboardService lacDashboardService, MeetingNoteService meetingNoteService, PaginationService paginationService) {
        this.userService = userService;
        this.lacDashboardService = lacDashboardService;
        this.meetingNoteService = meetingNoteService;
        this.paginationService = paginationService;
    }

    @GetMapping("/lac/dashboard")
    public ModelAndView lacDashboard(@RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size,
                                     Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);

            userService.authoriseUser(user, List.of(UserType.LAC));

            User socialWorkerUser = lacDashboardService.getSocialWorkerForLoggedInLAC(user);

            ArrayList<MeetingNote> meetingNotes = meetingNoteService.getAllMeetingNotesForUser(user.getUid(), socialWorkerUser.getUid());

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

            model.addAttribute("user", user);
            model.addAttribute("socialWorkerUser", socialWorkerUser);
            model.addAttribute("meetingNotePage", meetingNotePage);

            return new ModelAndView("lacDashboard");
        }
        return new ModelAndView("redirect:/login");
    }
}
