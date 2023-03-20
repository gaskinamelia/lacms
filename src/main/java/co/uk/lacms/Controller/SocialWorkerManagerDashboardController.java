package co.uk.lacms.Controller;

import co.uk.lacms.Entity.LacUser;
import co.uk.lacms.Entity.MeetingNote;
import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.FilterManagerDashboardForm;
import co.uk.lacms.Form.SearchForm;
import co.uk.lacms.Form.UpdateLacForm;
import co.uk.lacms.Service.PaginationService;
import co.uk.lacms.Service.SearchService;
import co.uk.lacms.Service.SocialWorkerManagerDashboardService;
import co.uk.lacms.Service.UserService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class SocialWorkerManagerDashboardController {

    @Autowired
    UserService userService;

    @Autowired
    SocialWorkerManagerDashboardService socialWorkerManagerDashboardService;

    @Autowired
    PaginationService paginationService;

    @Autowired
    SearchService searchService;

    private String idTokenLoggedInUser;

    public SocialWorkerManagerDashboardController(UserService userService, SocialWorkerManagerDashboardService socialWorkerManagerDashboardService, PaginationService paginationService, SearchService searchService) {
        this.userService = userService;
        this.socialWorkerManagerDashboardService = socialWorkerManagerDashboardService;
        this.paginationService = paginationService;
        this.searchService = searchService;
    }

    @GetMapping("/swm/dashboard/assigned_lacs")
    public ModelAndView managerDashboardAssignedLacs(@RequestParam("page") Optional<Integer> page,
                                                     @RequestParam("size") Optional<Integer> size,
                                                     Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        model.addAttribute("dashboardLink", "/swm/dashboard/assigned_lacs");
        model.addAttribute("assignedLacs", true);

        return managerDashboardView(new FilterManagerDashboardForm(false, true), page, size, model);

    }

    @GetMapping("/swm/dashboard/unassigned_lacs")
    public ModelAndView managerDashboardUnassignedLacs(@RequestParam("page") Optional<Integer> page,
                                                       @RequestParam("size") Optional<Integer> size,
                                                       Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        model.addAttribute("dashboardLink", "/swm/dashboard/unassigned_lacs");
        model.addAttribute("unassignedLacs", true);

        return managerDashboardView(new FilterManagerDashboardForm(true, false), page, size, model);
    }

    private ModelAndView managerDashboardView(FilterManagerDashboardForm filterForm, Optional<Integer> page, Optional<Integer> size, Model model) {

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW_MANAGER));

            ArrayList<LacUser> lacUsers = socialWorkerManagerDashboardService.getFilteredLacUsers(filterForm);

            ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());

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
            model.addAttribute("filterManagerDashboardForm", filterForm);
            model.addAttribute("updateLacForm", new UpdateLacForm());
            model.addAttribute("socialWorkerUsers", socialWorkerUsers);
            model.addAttribute("searchForm", new SearchForm());

            return new ModelAndView("swmDashboard");
        }
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/swm/update/lac_user")
    public ModelAndView updateLacUser(@Valid @ModelAttribute("updateLacForm") UpdateLacForm updateLacForm,
                                      BindingResult result,
                                      Model model) {

        if(result.hasErrors()){
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW_MANAGER));

            ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());

            model.addAttribute("user", user);
            model.addAttribute("updateLacForm", updateLacForm);
            model.addAttribute("socialWorkerUsers", socialWorkerUsers);

            return new ModelAndView("/swmDashboard");
        }

        socialWorkerManagerDashboardService.assignSocialWorkerToLac(updateLacForm.getLacUid(), updateLacForm.getSwUid());

        return new ModelAndView("redirect:/swm/dashboard/unassigned_lacs");
    }

    @GetMapping("/swm/search")
    public ModelAndView searchDashboard(@RequestParam("searchQuery") String searchQuery,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size,
                                        Model model) {
        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW_MANAGER));

            ArrayList<LacUser> lacUsers = searchService.searchLacUser(searchQuery);

            ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());

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
            model.addAttribute("updateLacForm", new UpdateLacForm());
            model.addAttribute("socialWorkerUsers", socialWorkerUsers);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("dashboardLink", "/swm/search?searchQuery="+ searchQuery);

            return new ModelAndView("swmSearch");
        }
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/swm/search")
    public ModelAndView searchDashboard(@ModelAttribute("searchForm") SearchForm searchForm,
                                        Model model) {

        return new ModelAndView("redirect:/swm/search?searchQuery=" + searchForm.getSearchQuery());
    }

    //TODO: View social worker profile by uid, display all lacs assigned to social worker and maybe count how many assigned.

}
