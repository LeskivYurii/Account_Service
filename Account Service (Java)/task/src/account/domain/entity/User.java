package account.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private int id;
    private String email;
    private String name;
    private String lastname;
    @JsonIgnore
    private int signInAttempt = 0;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private boolean isUserEnabled = true;
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Payroll> payments;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_groups",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> roles = new HashSet<>();

    public User() {
    }

    public User(String name, String lastname, String email, String password, Group group) {
        this.name = name;
        this.lastname = lastname;
        this.email = email.toLowerCase();
        this.password = password;
        this.roles.add(group);
    }

    public User(String name, String lastname, String email, String password, Set<Payroll> payments, Group group) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.payments = payments;
        this.roles.add(group);
    }

    public User(String email, String name, String lastname, int signInAttempt, String password, boolean isUserEnabled, Set<Payroll> payments, Set<Group> roles) {
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.signInAttempt = signInAttempt;
        this.password = password;
        this.isUserEnabled = isUserEnabled;
        this.payments = payments;
        this.roles = roles;
    }

    public int getSignInAttempt() {
        return signInAttempt;
    }

    public void setSignInAttempt(int signInAttempt) {
        this.signInAttempt = signInAttempt;
    }

    public Set<Group> getRoles() {
        return roles;
    }

    public void setRoles(Set<Group> roles) {
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Payroll> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payroll> payments) {
        this.payments = payments;
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

    @JsonGetter("roles")
    public List<String> getRolesName() {
        return roles.stream().map(Group::getName).sorted().toList();
    }

    @JsonIgnore
    public boolean isUserEnabled() {
        return isUserEnabled;
    }

    public void setUserEnabled(boolean userEnabled) {
        isUserEnabled = userEnabled;
    }
}
