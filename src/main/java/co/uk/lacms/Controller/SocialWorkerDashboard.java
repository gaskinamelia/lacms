package co.uk.lacms.Controller;

import co.uk.lacms.Entity.LacUser;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.SearchForm;
import co.uk.lacms.Form.UpdateLacForm;
import co.uk.lacms.Service.PaginationService;
import co.uk.lacms.Service.SearchService;
import co.uk.lacms.Service.SocialWorkerDashboardService;
import co.uk.lacms.Service.UserService;
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
public class SocialWorkerDashboard {

    @Autowired
    UserService userService;

    @Autowired
    SocialWorkerDashboardService socialWorkerDashboardService;

    @Autowired
    PaginationService paginationService;

    @Autowired
    SearchService searchService;

    private String idTokenLoggedInUser;

    public SocialWorkerDashboard(UserService userService, SocialWorkerDashboardService socialWorkerDashboardService, PaginationService paginationService, SearchService searchService) {
        this.userService = userService;
        this.socialWorkerDashboardService = socialWorkerDashboardService;
        this.paginationService = paginationService;
        this.searchService = searchService;
    }


    @GetMapping("/sw/dashboard")
    public ModelAndView socialWorkerDashboard(@RequestParam("page") Optional<Integer> page,
                                              @RequestParam("size") Optional<Integer> size,
                                              @RequestParam("searchQuery") Optional<String> searchQuery,
                                              Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW));

            ArrayList<LacUser> lacs = socialWorkerDashboardService.getAllLacForLoggedInSocialWorker(user);

            int currentPage = page.orElse(1);
            int pageSize = size.orElse(10);

            Page<LacUser> lacPage = paginationService.paginateLacUsers(lacs, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("lacPage", lacPage);

            int totalPages = lacPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            model.addAttribute("user", user);
            model.addAttribute("searched", false);

            return new ModelAndView("swDashboard");
        }
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/sw/search")
    public ModelAndView searchDashboard(@RequestParam("searchQuery") String searchQuery,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size,
                                        Model model) {
        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW));

            ArrayList<LacUser> lacUsers = searchService.searchLacUserForSocialWorkerUser(searchQuery, user);

            int currentPage = page.orElse(1);
            int pageSize = size.orElse(10);

            Page<LacUser> lacPage = paginationService.paginateLacUsers(lacUsers, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("lacPage", lacPage);

            int totalPages = lacPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            model.addAttribute("user", user);
            model.addAttribute("searched", true);
            model.addAttribute("searchQuery", searchQuery);

            return new ModelAndView("swDashboard");
        }
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/sw/search")
    public ModelAndView searchDashboard(@ModelAttribute("searchForm") SearchForm searchForm,
                                        Model model) {

        return new ModelAndView("redirect:/sw/search?searchQuery=" + searchForm.getSearchQuery());
    }

    //TODO: Search meeting notes

}
