package co.uk.lacms.Controller;

import co.uk.lacms.Entity.User;
import co.uk.lacms.Entity.UserType;
import co.uk.lacms.Form.LoginForm;
import co.uk.lacms.Service.FirebaseAuthManager;
import co.uk.lacms.Service.UserService;
import com.google.firebase.ErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    FirebaseAuthManager firebaseAuthManager;

    public LoginController(UserService userService, FirebaseAuthManager firebaseAuthManager) {
        this.userService = userService;
        this.firebaseAuthManager = firebaseAuthManager;
    }

    @GetMapping("/")
    public ModelAndView index(Model model) {
        if(userService.getLoggedInToken() == null || userService.getLoggedInToken().isEmpty()) {
            return new ModelAndView("redirect:/login");
        } else {
            UserType loggedInUserType = userService.getUserTypeByToken(userService.getLoggedInToken());

            if(loggedInUserType.equals(UserType.SW)) {
                return new ModelAndView("redirect:/sw/dashboard");
            } else if (loggedInUserType.equals(UserType.SW_MANAGER)) {
                return new ModelAndView("redirect:/swm/dashboard/unassigned_lacs");
            } else if (loggedInUserType.equals(UserType.LAC)) {
                return new ModelAndView("redirect:/lac/dashboard");
            } else {
                return new ModelAndView("redirect:/dashboard");
            }
        }
    }

    @GetMapping("/login")
    public ModelAndView loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return new ModelAndView("login");
    }

    @PostMapping("/login/submit")
    public ModelAndView login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                              BindingResult result,
                              Model model) {

        if(result.hasErrors()){
            return new ModelAndView("/login");
        }

        String token = null;
        try {
            token = userService.authenticateUser(loginForm.getEmail(), loginForm.getHashedPassword());
        } catch (FirebaseAuthException e) {
            if(e.getErrorCode().equals(ErrorCode.UNAUTHENTICATED)) {
                result.rejectValue("email", null, "Your email and/or password is incorrect.");
                result.rejectValue("hashedPassword", null, "Your email and/or password is incorrect.");
                return new ModelAndView("/login");
            }
        }

        userService.setLoggedInToken(token);

        UserType loggedInUserType = userService.getUserTypeByToken(token);

        if(loggedInUserType.equals(UserType.SW)) {
            return new ModelAndView("redirect:/sw/dashboard?success");
        } else if (loggedInUserType.equals(UserType.SW_MANAGER)) {
            return new ModelAndView("redirect:/swm/dashboard/unassigned_lacs?success");
        } else if (loggedInUserType.equals(UserType.LAC)) {
            return new ModelAndView("redirect:/lac/dashboard?success");
        } else {
            return new ModelAndView("redirect:/dashboard?success");
        }

    }

    @GetMapping("/register")
    public ModelAndView showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return new ModelAndView("register");
    }

    @PostMapping("/register/save")
    public ModelAndView registration(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) throws FirebaseAuthException {

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return new ModelAndView("/register");
        }

        try {
            userService.signUpUser(user);
        } catch (FirebaseAuthException e) {
            if(e.getErrorCode().equals(ErrorCode.ALREADY_EXISTS)) {
                result.rejectValue("email", null, "There is already an account registered with the same email.");
            } else if(e.getErrorCode().equals(ErrorCode.INVALID_ARGUMENT)) {
                result.rejectValue("email", null, "Please enter a valid email address.");
            }
        }

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return new ModelAndView("/register");
        }

        return new ModelAndView("redirect:/register?success");
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        userService.setLoggedInToken(null);
        return new ModelAndView("redirect:/login");
    }

}
