package account.service;

import account.domain.*;
import account.domain.entity.Audit;
import account.domain.entity.Group;
import account.domain.entity.User;
import account.exception.custom.*;
import account.repository.AuditRepository;
import account.repository.GroupRepository;
import account.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class AccountService {

    public static final String ROLE_ADMIN = "ROLE_ADMINISTRATOR";
    public static final String ANONYMOUS = "Anonymous";
    public static final String GRANT_ROLE_MESSAGE = "Grant role %s to %s";
    public static final String REMOVE_ROLE_MESSAGE = "Remove role %s from %s";
    public static final String CREATE_USER = "CREATE_USER";
    public static final String CREATE_USER_API = "/api/auth/signup";
    public static final String GRANT_ROLE = "GRANT_ROLE";
    public static final String REMOVE_ROLE = "REMOVE_ROLE";
    public static final String LOCK_USER = "LOCK_USER";
    public static final String UNLOCK_USER = "UNLOCK_USER";
    public static final String CHANGE_ROLE_API = "/api/admin/user/role";
    public static final String DELETE_USER = "DELETE_USER";
    public static final String DELETE_USER_API = "/api/admin/user";
    public static final String CHANGE_PASSWORD_API = "/api/auth/changepass";
    public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
    public static final String CHANGE_USER_ACCESS_API = "/api/admin/user/access";


    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private AuditRepository auditRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepository, GroupRepository groupRepository, AuditRepository auditRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.auditRepository = auditRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(final SignUpDto sign) {
        return Optional
                .of(sign)
                .filter(sign1 -> !userRepository.existsByEmail(sign1.getEmail().toLowerCase()))
                .map(sign1 -> new User(sign1.getName(), sign1.getLastname(), sign1.getEmail().toLowerCase(),
                        passwordEncoder.encode(sign1.getPassword()), getRole()))
                .map(user -> {
                    auditRepository.save(toAudit(CREATE_USER, ANONYMOUS, sign.getEmail().toLowerCase(), CREATE_USER_API));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserExistException("User exist!"));
    }

    private Group getRole() {
        return groupRepository.findByName(userRepository.count() > 0
                        ? Role.ROLE_USER.toString()
                        : Role.ROLE_ADMINISTRATOR.toString())
                .orElse(null);
    }

    @Transactional
    public ChangePasswordResponse changePassword(final ChangePasswordRequest changePasswordRequest, final String email) {
        return Optional
                .of(email)
                .flatMap(email1 -> userRepository.findUserByEmailIgnoreCase(email))
                .map(user -> {
                    validateNewPassword(changePasswordRequest.getNewPassword(), user);
                    user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                    auditRepository.save(toAudit(CHANGE_PASSWORD, email, email, CHANGE_PASSWORD_API));
                    userRepository.save(user);
                    return new ChangePasswordResponse(email);
                })
                .orElseThrow(() -> new UserNotFoundException("User with that email doesn't exist"));
    }

    @Transactional
    public User changeUserRole(final ChangeRoleRequest changeRoleRequest, UserDetailsAdapter userDetailsAdapter) {
        User user = userRepository.findUserByEmailIgnoreCase(changeRoleRequest.getUser())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        validateChangeRoleRequest(changeRoleRequest, user);
        if (changeRoleRequest.getOperation().equals(ChangeRoleRequest.Operation.GRANT)) {
            user.getRoles().add(new Group(changeRoleRequest.getRole().toString()));
        } else if (changeRoleRequest.getOperation().equals(ChangeRoleRequest.Operation.REMOVE)) {
            user.getRoles().remove(new Group(changeRoleRequest.getRole().toString()));
        }
        boolean isGrantOperation = ChangeRoleRequest.Operation.GRANT.equals(changeRoleRequest.getOperation());
        String action = isGrantOperation ? GRANT_ROLE : REMOVE_ROLE;
        String auditObject = isGrantOperation ? GRANT_ROLE_MESSAGE : REMOVE_ROLE_MESSAGE;
        auditRepository.save(toAudit(action, userDetailsAdapter.getUsername(),
                auditObject.formatted(changeRoleRequest.getRole().toString().replace("ROLE_", ""),
                        changeRoleRequest.getUser()), CHANGE_ROLE_API));

        return userRepository.save(user);
    }

    private void validateChangeRoleRequest(final ChangeRoleRequest changeRoleRequest, final User user) {
        if (changeRoleRequest.getOperation().equals(ChangeRoleRequest.Operation.REMOVE) &&
                Role.ROLE_ADMINISTRATOR.equals(changeRoleRequest.getRole())) {
            throw new RemoveAdminUserException("Can't remove ADMINISTRATOR role!");
        } else if (!groupRepository.existsByName(changeRoleRequest.getRole().toString())) {
            throw new RoleNotFoundException("Role not found!");
        } else if (changeRoleRequest.getOperation().equals(ChangeRoleRequest.Operation.REMOVE) &&
                !user.getRolesName().contains(changeRoleRequest.getRole().toString())) {
            throw new ChangeRoleException("The user does not have a role!");
        } else if (user.getRoles().size() <= 1 && changeRoleRequest.getOperation().equals(ChangeRoleRequest.Operation.REMOVE)) {
            throw new ChangeRoleException("The user must have at least one role!");
        } else if ((user.getRolesName().contains(Role.ROLE_ADMINISTRATOR.toString()) &&
                (Role.ROLE_USER.equals(changeRoleRequest.getRole()) ||
                Role.ROLE_ACCOUNTANT.equals(changeRoleRequest.getRole()) ||
                        Role.ROLE_AUDITOR.equals(changeRoleRequest.getRole()))) ||
                ((user.getRolesName().contains(Role.ROLE_USER.toString()) ||
                        user.getRolesName().contains(Role.ROLE_ACCOUNTANT.toString()) ||
                        user.getRolesName().contains(Role.ROLE_AUDITOR.toString()))
                        && changeRoleRequest.getRole().equals(Role.ROLE_ADMINISTRATOR))) {
            throw new ChangeRoleException("The user cannot combine administrative and business roles!");
        }
    }

    public Map<String, String> deleteUser(final String userEmail, final UserDetailsAdapter userDetailsAdapter) {
        return Optional
                .of(userEmail)
                .flatMap(userRepository::findUserByEmailIgnoreCase)
                .map(user -> {
                    if (user.getRolesName().contains(ROLE_ADMIN)) {
                        throw new RemoveAdminUserException("Can't remove ADMINISTRATOR role!");
                    }
                    auditRepository.save(toAudit(DELETE_USER, userDetailsAdapter.getUsername(), userEmail.toLowerCase(),
                            DELETE_USER_API));
                    userRepository.delete(user);

                    return Map.of("user", userEmail, "status", "Deleted successfully!");
                })
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAllByOrderById();
    }

    @Transactional
    public Map<String, String> changeUserAccess(final AccessRequest accessRequest, UserDetailsAdapter userDetailsAdapter) {
        User user = userRepository
                .findUserByEmailIgnoreCase(accessRequest.getUser())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (user.getRolesName().contains(ROLE_ADMIN)) {
            throw new ChangeUserAccessException("Can't lock the ADMINISTRATOR!");
        }
        String action = "";
        String messageAudit = "";
        if (accessRequest.getOperation().equals(AccessRequest.Operation.LOCK)) {
            user.setUserEnabled(false);
            action = LOCK_USER;
            messageAudit = "Lock user " + user.getEmail();
        } else if (accessRequest.getOperation().equals(AccessRequest.Operation.UNLOCK)) {
            user.setUserEnabled(true);
            user.setSignInAttempt(0);
            messageAudit = "Unlock user " + user.getEmail();
            action = UNLOCK_USER;
        }
        auditRepository.save(toAudit(action, userDetailsAdapter.getUsername(), messageAudit, CHANGE_USER_ACCESS_API));
        userRepository.save(user);
        String responseMessage = "User %s %s!".formatted(accessRequest.getUser().toLowerCase(),
                accessRequest.getOperation().equals(AccessRequest.Operation.UNLOCK) ? "unlocked" : "locked");

        return Map.of("status", responseMessage);
    }

    private void validateNewPassword(String newPassword, User user) {
        if (passwordEncoder.matches(newPassword, user.getPassword()))
            throw new ChangePasswordException("The passwords must be different!");
        else if (newPassword.length() < 12) {
            throw new ChangePasswordException("Password length must be 12 chars minimum!");
        }
    }

    private Audit toAudit(String action, String subject, String object, String path) {
        return new Audit(LocalDate.now(), action, subject, object, path);
    }

}
