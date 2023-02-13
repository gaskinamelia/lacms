package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.LoginForm;
import co.uk.lacms.Service.LoginService;
import co.uk.lacms.Service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    UserService userService;

    private String idTokenLoggedInUser;

    public LoginController(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if(idTokenLoggedInUser != null) {
            User user = userService.getUserByToken(idTokenLoggedInUser);
            model.addAttribute("user", user);
            model.addAttribute("userExists", true);
        } else {
            model.addAttribute("userExists", false);
        }

        return "dashboard";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());

        return "login";
    }

    @PostMapping("/login/submit")
    public String login(@ModelAttribute("loginForm") LoginForm loginForm, Model model) throws FirebaseAuthException {
        String token = userService.autheticateUser(loginForm.getEmail(), loginForm.getHashedPassword());

        if(!token.isEmpty()) {

            idTokenLoggedInUser = token;

            return "redirect:/dashboard?success";
        } else {
            return "redirect:/home?error";
        }

    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) throws FirebaseAuthException {

        UserRecord existingUser = userService.getUserByEmail(user.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return "/register";
        }

        userService.signUpUser(user);
        return "redirect:/register?success";
    }

}
