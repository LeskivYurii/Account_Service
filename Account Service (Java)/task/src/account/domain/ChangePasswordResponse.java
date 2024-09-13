package account.domain;

import jakarta.persistence.JoinColumn;

public class ChangePasswordResponse {

    private String email;
    @JoinColumn(name = "status")
    public  String status = "The password has been updated successfully";

    public ChangePasswordResponse(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

}
