package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Service.SocialWorkerDashboardService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class SocialWorkerDashboard {

    @Autowired
    UserService userService;

    @Autowired
    SocialWorkerDashboardService socialWorkerDashboardService;
    private String idTokenLoggedInUser;

    public SocialWorkerDashboard(UserService userService, SocialWorkerDashboardService socialWorkerDashboardService) {
        this.userService = userService;
        this.socialWorkerDashboardService = socialWorkerDashboardService;
    }


    @GetMapping("/sw/dashboard")
    public ModelAndView socialWorkerDashboard(Model model) {
        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);

            ArrayList<User> lacs = socialWorkerDashboardService.getAllLacForLoggedInSocialWorker(user);

            model.addAttribute("user", user);
            model.addAttribute("lacs", lacs);

            return new ModelAndView("swDashboard");
        }
        return new ModelAndView("redirect:/login");
    }

}
