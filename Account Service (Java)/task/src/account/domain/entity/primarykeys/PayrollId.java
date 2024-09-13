package account.domain.entity.primarykeys;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class PayrollId implements Serializable {

    private String employee;
    private YearMonth period;

    public PayrollId() {
    }

    public PayrollId(String employee, String period) {
        this.employee = employee;
        this.period = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
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

    public void setPeriod(String period) {
        this.period = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
    }
}
