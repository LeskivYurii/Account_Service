package account.cotroller;

import account.domain.entity.Audit;
import account.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuditResource {

    @Autowired
    private AuditRepository auditRepository;

    @GetMapping("/security/events/")
    public List<Audit> getAudits() {
        return auditRepository.findAll();
    }
}
