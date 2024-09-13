package account.domain.entity;

import account.domain.entity.primarykeys.PayrollId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "payrolls")
@IdClass(PayrollId.class)
public class Payroll {

    @Id
    private String employee;
    @Id
    private YearMonth period;
    @Positive
    private Long salary;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payroll payroll1 = (Payroll) o;

        if (!employee.equals(payroll1.employee)) return false;
        return period.equals(payroll1.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }

    public Payroll() {
    }

    public Payroll(String employee, String period, Long salary) {
        this.employee = employee;
        this.period = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
        this.salary = salary;
    }


    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period.format(DateTimeFormatter.ofPattern("MM-yyyy"));
    }

    public String getFullNamePeriod() {
        return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy"));
    }

    public void setPeriod(String period) {
        this.period = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

}
