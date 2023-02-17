package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.LoginForm;
import co.uk.lacms.Service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return new ModelAndView("login");
    }

    @PostMapping("/login/submit")
    public ModelAndView login(@ModelAttribute("loginForm") LoginForm loginForm, Model model) throws FirebaseAuthException {
        String token = userService.authenticateUser(loginForm.getEmail(), loginForm.getHashedPassword());

        if(!token.isEmpty()) {

            userService.setLoggedInToken(token);

            UserType loggedInUserType = userService.getUserTypeByToken(token);

            if(loggedInUserType.equals(UserType.SW)) {
                return new ModelAndView("redirect:/dashboardSocialWorker?success");
            } else if (loggedInUserType.equals(UserType.SW_MANAGER)) {
                return new ModelAndView("redirect:/swm/dashboard?success");
            } else if (loggedInUserType.equals(UserType.LAC)) {
                return new ModelAndView("redirect:/dashboardLAC?success");
            } else {
                return new ModelAndView("redirect:/dashboard?success");
            }
        } else {
            return new ModelAndView("redirect:/login?error");
        }

    }

    @GetMapping("/register")
    public ModelAndView showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return new ModelAndView("register");
    }

    @PostMapping("/register/save")
    public ModelAndView registration(@ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) throws FirebaseAuthException {

        UserRecord existingUser = userService.getUserRecordByEmail(user.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return new ModelAndView("/register");
        }

        userService.signUpUser(user);
        return new ModelAndView("redirect:/register?success");
    }

    @PostMapping("/logout")
    public ModelAndView logout() {
        userService.setLoggedInToken(null);
        return new ModelAndView("redirect:/login");
    }

}
