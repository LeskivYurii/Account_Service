package account.domain;

import account.validation.SecuredPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

public class ChangePasswordRequest {

    @SecuredPassword
    @JsonProperty("new_password")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
