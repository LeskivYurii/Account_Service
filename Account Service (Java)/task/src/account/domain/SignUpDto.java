package account.domain;

import account.validation.SecuredPassword;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpDto {

    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    @Pattern(regexp = "\\S+@acme.com")
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @SecuredPassword
    private String password;

    public SignUpDto() {
    }

    public SignUpDto(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
