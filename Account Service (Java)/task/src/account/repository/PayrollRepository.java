package account.repository;

import account.domain.entity.Payroll;
import account.domain.entity.primarykeys.PayrollId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, PayrollId> {

    List<Payroll> findByEmployee(String employee);
}
