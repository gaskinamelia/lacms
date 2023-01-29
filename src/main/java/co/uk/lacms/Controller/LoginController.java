package co.uk.lacms.Controller;

import co.uk.lacms.Service.LoginService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class LoginController {

    @Autowired
    LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password){

        return "login";
    }

    @PostMapping("/create")
    public String createUser() {
        return "create";
    }

    @GetMapping("/getUser")
    public String getUserPage(@RequestParam("email") String email, Model model) throws FirebaseAuthException {

        UserRecord user = loginService.getUserByEmail(email);
        model.addAttribute("User", user.getEmail());

       return "userPage";
    }

}
