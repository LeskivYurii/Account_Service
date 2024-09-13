package account.service;

import account.domain.Role;
import account.domain.UserDetailsAdapter;
import account.domain.entity.Group;
import account.domain.entity.User;
import account.repository.UserRepository;
import account.security.AuthenticationFailureListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository
                .findUserByEmailIgnoreCase(username)
                .orElseGet(() -> new User(" ", " ", username, " ", new Group(Role.ROLE_USER.toString())));

        if (user.getSignInAttempt() >= AuthenticationFailureListener.MAX_ATTEMPT && user.isUserEnabled()) {
            user.setUserEnabled(false);
            userRepository.save(user);
        }

        return new UserDetailsAdapter(user);
    }

}
