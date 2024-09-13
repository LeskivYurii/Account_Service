package account.cotroller;

import account.domain.entity.Payroll;
import account.domain.UserDetailsAdapter;
import account.service.PayrollService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class PayrollResource {

    private PayrollService payrollService;

    public PayrollResource(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> getPayrolls(@AuthenticationPrincipal UserDetailsAdapter userDetailsAdapter,
                                      @RequestParam(required = false)  @Pattern(regexp = "(0[1-9]|1[012])-\\d+") String period) {
        var result = payrollService.getPayrolls(userDetailsAdapter, period);
        return ResponseEntity
                .ok()
                .body(result.size() == 1 ? result.get(0) : result);
    }

    @PostMapping("/acct/payments")
    public Map<String, String> createPayrolls(@RequestBody final List<@Valid Payroll> payrolls) {
        return payrollService.createPayrolls(payrolls);
    }

    @PutMapping("/acct/payments")
    public Map<String, String> updatePayrolls(@Valid @RequestBody final Payroll payroll) {
        return payrollService.updatePayrolls(payroll);
    }

}
