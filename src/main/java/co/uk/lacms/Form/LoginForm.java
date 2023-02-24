package co.uk.lacms.Form;

import javax.validation.constraints.NotEmpty;

public class LoginForm {

    @NotEmpty(message = "Please insert your email address.")
    private String email;
    @NotEmpty(message = "Please insert your password.")
    private String hashedPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
