package co.uk.lacms.Controller;

import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.SearchForm;
import co.uk.lacms.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    SearchService searchService;

    private String idTokenLoggedInUser;

    public LacDashboardController(UserService userService, LacDashboardService lacDashboardService, MeetingNoteService meetingNoteService, PaginationService paginationService, SearchService searchService) {
        this.userService = userService;
        this.lacDashboardService = lacDashboardService;
        this.meetingNoteService = meetingNoteService;
        this.paginationService = paginationService;
        this.searchService = searchService;
    }

    @GetMapping("/lac/dashboard")
    public ModelAndView lacDashboard(@RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size,
                                     @RequestParam("searchQuery") Optional<String> searchQuery,
                                     @RequestParam("archived") Optional<Boolean> viewArchived,
                                     Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);

            userService.authoriseUser(user, List.of(UserType.LAC));

            Optional<User> socialWorkerUser = lacDashboardService.getSocialWorkerForLoggedInLAC(user);

            ArrayList<MeetingNote> meetingNotes = null;

            if(socialWorkerUser.isPresent() && (searchQuery.isPresent() && viewArchived.isPresent())) {
                meetingNotes = searchService.searchMeetingNotes(user.getUid(), socialWorkerUser.get().getUid(), searchQuery.get(), viewArchived.get());
                model.addAttribute("searched", true);
            } else {
                meetingNotes = meetingNoteService.getAllMeetingNotesForUser(user.getUid(), socialWorkerUser.orElse(null));
                model.addAttribute("searched", false);
            }

            int currentPage = page.orElse(1);
            int pageSize = size.orElse(10);

            Page<MeetingNote> meetingNotePage = paginationService.paginateMeetingNotes(meetingNotes, PageRequest.of(currentPage - 1, pageSize));

            int totalPages = meetingNotePage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            model.addAttribute("user", user);
            model.addAttribute("socialWorkerUser", socialWorkerUser.orElse(null));
            model.addAttribute("meetingNotePage", meetingNotePage);
            model.addAttribute("searchQuery", searchQuery.orElse(""));

            return new ModelAndView("lacDashboard");
        }
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/lac/search")
    public ModelAndView searchDashboard(@ModelAttribute("searchForm") SearchForm searchForm,
                                        Model model) {

        return new ModelAndView("redirect:/lac/dashboard?searchQuery=" + searchForm.getSearchQuery() + "&archived=" + searchForm.isViewArchived());
    }
}