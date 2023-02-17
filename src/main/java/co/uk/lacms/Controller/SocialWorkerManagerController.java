package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Form.UpdateLacForm;
import co.uk.lacms.Service.SocialWorkerManagerDashboardService;
import co.uk.lacms.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class SocialWorkerManagerController {

    @Autowired
    UserService userService;

    @Autowired
    SocialWorkerManagerDashboardService socialWorkerManagerDashboardService;

    private String idTokenLoggedInUser;

    public SocialWorkerManagerController(UserService userService, SocialWorkerManagerDashboardService socialWorkerManagerDashboardService) {
        this.userService = userService;
        this.socialWorkerManagerDashboardService = socialWorkerManagerDashboardService;
    }

    @GetMapping("/swm/dashboard")
    public ModelAndView socialWorkerManagerDashboard(Model model) {

        idTokenLoggedInUser = userService.getLoggedInToken();

        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);

            ArrayList<User> lacsWithoutSW = socialWorkerManagerDashboardService.getAllLACWithUnassignedSocialWorker();

            ArrayList<User> socialWorkerUsers = socialWorkerManagerDashboardService.getAllSocialWorkerUsersForManagerUser(user.getUid());

            model.addAttribute("user", user);
            model.addAttribute("lacsWithoutSW", lacsWithoutSW);
            model.addAttribute("updateLacForm", new UpdateLacForm());
            model.addAttribute("socialWorkerUsers", socialWorkerUsers);

            return new ModelAndView("swmDashboard");
        }

        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/swm/update/lac_user")
    public ModelAndView updateLacUser(@ModelAttribute("updateLacForm") UpdateLacForm updateLacForm, Model model) {

        socialWorkerManagerDashboardService.assignSocialWorkerToLac(updateLacForm.getLacUid(), updateLacForm.getSwUid());

        return new ModelAndView("redirect:/swm/dashboard");
    }
}
