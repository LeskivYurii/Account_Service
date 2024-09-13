package account.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ChangeRoleRequest {

    @NotBlank
    private String user;

    private Role role;
    private Operation operation;

    public ChangeRoleRequest() {
    }

    public ChangeRoleRequest(String user, String role, Operation operation) {
        this.user = user;
        this.role = Role.valueOf("ROLE_" + role.toUpperCase());
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getUser() {
        return user.toLowerCase();
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = Role.valueOf("ROLE_" + role.toUpperCase());
    }

    public enum Operation {
        GRANT, REMOVE;
    }
}


