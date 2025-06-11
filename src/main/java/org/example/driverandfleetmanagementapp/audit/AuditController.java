package org.example.driverandfleetmanagementapp.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditRepository;


    @GetMapping
    public Page<AuditLog> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        return auditRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{entityType}/{entityId}")
    public List<AuditLog> getEntityAuditTrail(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        return auditRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
                entityType, entityId);
    }

    @GetMapping("/my-operations")
    public List<AuditLog> getMyOperations(Authentication auth) {
        String currentUser = auth.getName();
        return auditRepository.findByUserIdOrderByTimestampDesc(currentUser);
    }
}
