package account.cotroller;

import account.domain.*;
import account.domain.entity.User;
import account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountResource {

    @Autowired
    private AccountService accountService;

    @PostMapping("/auth/signup")
    public User signup(@Valid @RequestBody final SignUpDto signUpDto) {
        return accountService.create(signUpDto);
    }

    @PostMapping("/auth/changepass")
    public ChangePasswordResponse changePassword(@AuthenticationPrincipal final UserDetailsAdapter userDetailsAdapter,
                                                @Valid @RequestBody final ChangePasswordRequest changePasswordRequest) {
        return accountService.changePassword(changePasswordRequest, userDetailsAdapter.getUsername());
    }

    @PutMapping("/admin/user/role")
    public User changeUserRole(@AuthenticationPrincipal final UserDetailsAdapter userDetailsAdapter,
                               @Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
        return accountService.changeUserRole(changeRoleRequest, userDetailsAdapter);
    }

    @DeleteMapping("/admin/user/{user_email}")
    public Map<String, String> deleteUser(@AuthenticationPrincipal final UserDetailsAdapter userDetailsAdapter,
                                          @PathVariable(name = "user_email") final String userEmail) {
        return accountService.deleteUser(userEmail, userDetailsAdapter);
    }

    @GetMapping("/admin/user/")
    public List<User> getUsers() {
        return accountService.getAllUsers();
    }

    @PutMapping("/admin/user/access")
    public Map<String, String> changeAccountAccess(@AuthenticationPrincipal final UserDetailsAdapter userDetailsAdapter,
                                                   @Valid @RequestBody final AccessRequest accessRequest) {
        return accountService.changeUserAccess(accessRequest, userDetailsAdapter);
    }

}
