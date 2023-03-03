package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Service.PaginationService;
import co.uk.lacms.Service.SocialWorkerDashboardService;
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
public class SocialWorkerDashboard {

    @Autowired
    UserService userService;

    @Autowired
    SocialWorkerDashboardService socialWorkerDashboardService;

    @Autowired
    PaginationService paginationService;

    private String idTokenLoggedInUser;

    public SocialWorkerDashboard(UserService userService, SocialWorkerDashboardService socialWorkerDashboardService, PaginationService paginationService) {
        this.userService = userService;
        this.socialWorkerDashboardService = socialWorkerDashboardService;
        this.paginationService = paginationService;
    }


    @GetMapping("/sw/dashboard")
    public ModelAndView socialWorkerDashboard(@RequestParam("page") Optional<Integer> page,
                                              @RequestParam("size") Optional<Integer> size,
                                              Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            userService.authoriseUser(user, List.of(UserType.SW));

            ArrayList<User> lacs = socialWorkerDashboardService.getAllLacForLoggedInSocialWorker(user);

            int currentPage = page.orElse(1);
            int pageSize = size.orElse(10);

            Page<User> lacPage = paginationService.paginateUsers(lacs, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("lacPage", lacPage);

            int totalPages = lacPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            model.addAttribute("user", user);

            return new ModelAndView("swDashboard");
        }
        return new ModelAndView("redirect:/login");
    }



}
