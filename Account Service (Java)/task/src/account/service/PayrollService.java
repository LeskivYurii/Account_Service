package account.service;

import account.domain.entity.Audit;
import account.domain.entity.Payroll;
import account.domain.PayrollDto;
import account.domain.entity.User;
import account.domain.UserDetailsAdapter;
import account.domain.entity.primarykeys.PayrollId;
import account.exception.custom.PayrollException;
import account.exception.custom.UserNotFoundException;
import account.repository.AuditRepository;
import account.repository.PayrollRepository;
import account.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PayrollService {

    public static final String SALARY_FORMAT = "%s dollar(s) %s cent(s)";

    private UserRepository userRepository;
    private PayrollRepository payrollRepository;

    public PayrollService(UserRepository userRepository, PayrollRepository payrollRepository) {
        this.userRepository = userRepository;
        this.payrollRepository = payrollRepository;
    }

    @Transactional
    public List<PayrollDto> getPayrolls(final UserDetailsAdapter userDetailsAdapter, String period) {
        Set<Payroll> payrollSet = userDetailsAdapter.getUser().getPayments();
        List<Payroll> payrolls = Objects.isNull(period)
                ? payrollSet.stream().toList()
                : payrollSet.stream().filter(payroll -> payroll.getPeriod().equals(period)).findFirst().stream().toList();
        return payrolls
                .stream()
                .sorted(Comparator.comparing(Payroll::getPeriod).reversed())
                .map(employee -> toPayrollDto(employee, userDetailsAdapter))
                .toList();
    }

    @Transactional
    public Map<String, String> createPayrolls(final List<Payroll> payrolls) {
        validatePayrolls(payrolls);
        Map<String, User> users = new HashMap<>();
        payrolls.forEach(payroll -> {
            if(!users.containsKey(payroll.getEmployee())) {
                users.put(payroll.getEmployee(), userRepository.findUserByEmailIgnoreCase(payroll.getEmployee())
                        .orElseThrow(() -> new UserNotFoundException("User doesn't exist with email: " + payroll.getEmployee())));
            }
            payroll.setUser(users.get(payroll.getEmployee()));
        });

        payrollRepository.saveAll(payrolls);
        return Map.of("status", "Added successfully!");
    }

    @Transactional
    public Map<String, String> updatePayrolls(final Payroll payroll) {
        User user = userRepository.findUserByEmailIgnoreCase(payroll.getEmployee())
                        .orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        payroll.setUser(user);
        payrollRepository.save(payroll);
        return Map.of("status", "Updated successfully!");
    }

    private void validatePayrolls(List<Payroll> payrolls) {
        payrolls.forEach(payroll -> {
            if(payrollRepository.existsById(new PayrollId(payroll.getEmployee(), payroll.getPeriod()))) {
                throw new PayrollException("Payroll with %s employee and %s period already exists"
                        .formatted(payroll.getEmployee(), payroll.getPeriod()));
            }
        });
    }

    private PayrollDto toPayrollDto(Payroll payroll, UserDetailsAdapter userDetailsAdapter) {
        return new PayrollDto(userDetailsAdapter.getName(), userDetailsAdapter.getLastName(), payroll.getFullNamePeriod(),
                toSalary(payroll));
    }

    private String toSalary(Payroll payroll) {
        return Optional
                .of(payroll)
                .map(Payroll::getSalary)
                .map(salary -> String.format(SALARY_FORMAT, salary / 100, salary % 100))
                .orElse(null);
    }

}
