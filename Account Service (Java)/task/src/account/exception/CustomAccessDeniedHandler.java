package account.exception;

import account.domain.entity.Audit;
import account.repository.AuditRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private AuditRepository auditRepository;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        response.sendError(403, "Access Denied!");
        response.setStatus(403);
        auditRepository.save(new Audit(LocalDate.now(), "ACCESS_DENIED", auth.getName() == null ? "Anonymous" : auth.getName(),
                request.getRequestURI(), request.getRequestURI()));
    }
}
