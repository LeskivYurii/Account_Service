package account.security;

import account.domain.UserDetailsAdapter;
import account.domain.entity.Audit;
import account.domain.entity.User;
import account.repository.AuditRepository;
import account.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;

import static account.service.AccountService.ROLE_ADMIN;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    public static final int MAX_ATTEMPT = 5;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuditRepository auditRepository;


    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        Authentication userDetails = e.getAuthentication();
        User user = userRepository.findUserByEmailIgnoreCase(userDetails.getName())
                .orElse(null);
        int attempts = 0;
        if (user != null && !user.getRolesName().contains(ROLE_ADMIN)) {
            attempts = user.getSignInAttempt();
            user.setSignInAttempt(++attempts);
            userRepository.save(user);
        }
        saveFailedEvent(attempts, userDetails.getName().toLowerCase());
    }

    private void saveFailedEvent(int attempts, String username) {
        if (attempts <= MAX_ATTEMPT) {
            auditRepository.save(new Audit(LocalDate.now(), "LOGIN_FAILED", username,
                    request.getRequestURI(), request.getRequestURI()));
        }
        if (attempts == MAX_ATTEMPT) {
            auditRepository.save(new Audit(LocalDate.now(), "BRUTE_FORCE", username,
                    request.getRequestURI(), request.getRequestURI()));

        }
        if (attempts >= MAX_ATTEMPT) {
            auditRepository.save(new Audit(LocalDate.now(), "LOCK_USER", username,
                    "Lock user " + username, request.getRequestURI()));
            throw new LockedException("User account is locked");
        }
    }

}
