package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.FilterManagerDashboardForm;
import co.uk.lacms.Form.UpdateLacForm;
import co.uk.lacms.Service.PaginationService;
import co.uk.lacms.Service.SocialWorkerManagerDashboardService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private String idTokenLoggedInUser;

    public SocialWorkerManagerDashboardController(UserService userService, SocialWorkerManagerDashboardService socialWorkerManagerDashboardService, PaginationService paginationService) {
        this.userService = userService;
        this.socialWorkerManagerDashboardService = socialWorkerManagerDashboardService;
        this.paginationService = paginationService;
    }

    @GetMapping("/swm/dashboard")
    public ModelAndView socialWorkerManagerDashboard(@RequestParam("page") Optional<Integer> page,
                                                     @RequestParam("size") Optional<Integer> size,
                                                     Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW_MANAGER));

            ArrayList<User> lacsWithoutSW = socialWorkerManagerDashboardService.getAllLACWithUnassignedSocialWorker();
//            ArrayList<User> lacsWithSW = socialWorkerManagerDashboardService.getAllLACWithAssignedSocialWorker();
//
//            ArrayList<User> lacs = new ArrayList<>();
//            lacs.addAll(lacsWithSW);
//            lacs.addAll(lacsWithoutSW);

            ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());

            int currentPage = page.orElse(1);
            int pageSize = size.orElse(10);

            Page<User> lacPage = paginationService.paginateUsers(lacsWithoutSW, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("lacPage", lacPage);

            int totalPages = lacPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            model.addAttribute("user", user);
//            model.addAttribute("filterManagerDashboardForm", new FilterManagerDashboardForm(true, true));
            model.addAttribute("lacsWithoutSW", lacsWithoutSW);
//            model.addAttribute("lacsWithSW", lacsWithSW);
            model.addAttribute("updateLacForm", new UpdateLacForm());
            model.addAttribute("socialWorkerUsers", socialWorkerUsers);

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

            ArrayList<User> lacsWithoutSW = socialWorkerManagerDashboardService.getAllLACWithUnassignedSocialWorker();

            ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());

            model.addAttribute("user", user);
            model.addAttribute("lacsWithoutSW", lacsWithoutSW);
            model.addAttribute("updateLacForm", updateLacForm);
            model.addAttribute("socialWorkerUsers", socialWorkerUsers);

            return new ModelAndView("/swmDashboard");
        }

        socialWorkerManagerDashboardService.assignSocialWorkerToLac(updateLacForm.getLacUid(), updateLacForm.getSwUid());

        return new ModelAndView("redirect:/swm/dashboard");
    }

//    @PostMapping("swm/filter")
//    public ModelAndView filterDashboard(@ModelAttribute("filterManagerDashboard") FilterManagerDashboardForm filterManagerDashboardForm,
//                                      Model model) {
//
//        User user = userService.getUserByToken(idTokenLoggedInUser);
//        userService.authoriseUser(user, List.of(UserType.SW_MANAGER));
//
//        if(filterManagerDashboardForm.isUnassignedLacs() && filterManagerDashboardForm.isAssignedLacs()) {
//            model.addAttribute("lacsWithoutSW", socialWorkerManagerDashboardService.getAllLACWithUnassignedSocialWorker());
//            model.addAttribute("lacsWithSW", socialWorkerManagerDashboardService.getAllLACWithAssignedSocialWorker());
//        } else if(filterManagerDashboardForm.isAssignedLacs()) {
//            model.addAttribute("lacsWithSW", socialWorkerManagerDashboardService.getAllLACWithAssignedSocialWorker());
//        } else if(filterManagerDashboardForm.isUnassignedLacs()) {
//            model.addAttribute("lacsWithoutSW", socialWorkerManagerDashboardService.getAllLACWithUnassignedSocialWorker());
//        } else {
//            model.addAttribute("noFilterAdded");
//        }
//
//        ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());
//
//        model.addAttribute("user", user);
//        model.addAttribute("socialWorkerUsers", socialWorkerUsers);
//
//        return new ModelAndView("/swm/dashboard");
//    }
}
