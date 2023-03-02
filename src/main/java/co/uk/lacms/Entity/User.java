package co.uk.lacms.Entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class User {
    private String uid;

    @NotEmpty(message = "User's email cannot be empty.")
    private String email;
    @NotEmpty(message = "User's password cannot be empty.")
    private String password;

    @NotEmpty(message = "User's first name cannot be empty.")
    private String firstName;

    @NotEmpty(message = "User's last name cannot be empty.")
    private String lastName;

    @NotNull(message = "Please select a user type.")
    private UserType userType;
    private String token;

    public User() {

    }

    public User(String uid, String email, String firstName, String lastName, UserType userType, String token) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
