package co.uk.lacms.Service;

import co.uk.lacms.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Autowired
    private FirebaseAuthManager firebaseAuthManager;

    public LoginService(FirebaseAuth firebaseAuth, FirebaseAuthManager firebaseAuthManager) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseAuthManager = firebaseAuthManager;
    }

    /**
     * Logins user in using the firebaseAuthManager which returns a token.
     * @param email
     * @param password
     * @return a user token
     * @throws FirebaseAuthException
     */
    public String loginUser(String email, String password) throws FirebaseAuthException {
        return firebaseAuthManager.auth(email, password);
    }

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return firebaseAuth.getUserByEmail(email);
    }

    public User getUserByUid(String uid) {
        return firebaseAuthManager.getAccountInfo(uid);
    }

}
