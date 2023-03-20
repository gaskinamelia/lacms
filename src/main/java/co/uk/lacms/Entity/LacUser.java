package co.uk.lacms.Entity;

public class LacUser extends User {

    private User socialWorkerUser;

    public LacUser(String uid, String email, String firstName, String lastName, UserType userType, String token, User socialWorkerUser) {
        super(uid, email, firstName, lastName, userType, token);
        this.socialWorkerUser = socialWorkerUser;
    }

    public boolean hasSocialWorker() {
        return socialWorkerUser != null;
    }

    public User getSocialWorkerUser() {
        return socialWorkerUser;
    }
}
