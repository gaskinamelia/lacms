package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    @Autowired
    UserService userService;

    private String idTokenLoggedInUser;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/dashboard")
    public ModelAndView dashboard(Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            model.addAttribute("user", user);
        } else {
        }

        return new ModelAndView("dashboard");
    }

    @GetMapping("/dashboardSocialWorker")
    public String dashboardSocialWorker(Model model) {

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            model.addAttribute("user", user);

            return "dashboard";

        } else {
            return "redirect:/login?error";
        }

    }

}
