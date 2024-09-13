package account.security;

import account.domain.entity.User;
import account.exception.custom.UserNotFoundException;
import account.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationSuccessHandlerImpl implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        User user = userRepository.findUserByEmailIgnoreCase(event.getAuthentication().getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(user.getSignInAttempt() > 0) {
            user.setSignInAttempt(0);
            userRepository.save(user);
        }
    }

}
