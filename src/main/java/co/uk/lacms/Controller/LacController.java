package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Service.LacDashboardService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class LacController {

    @Autowired
    UserService userService;

    @Autowired
    LacDashboardService lacDashboardService;

    private String idTokenLoggedInUser;

    public LacController(UserService userService, LacDashboardService lacDashboardService) {
        this.userService = userService;
        this.lacDashboardService = lacDashboardService;
    }

    @GetMapping("/lac/dashboard")
    public ModelAndView lacDashboard(Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);

            User socialWorkerUser = lacDashboardService.getSocialWorkerForLoggedInLAC(user);

            model.addAttribute("user", user);
            model.addAttribute("socialWorkerUser", socialWorkerUser);

            return new ModelAndView("lacDashboard");
        }
        return new ModelAndView("redirect:/login");
    }
}
