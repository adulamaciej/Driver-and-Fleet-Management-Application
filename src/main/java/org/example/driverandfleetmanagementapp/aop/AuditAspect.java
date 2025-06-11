package org.example.driverandfleetmanagementapp.aop;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.driverandfleetmanagementapp.audit.AuditLog;
import org.example.driverandfleetmanagementapp.audit.AuditLogRepository;
import org.example.driverandfleetmanagementapp.audit.Auditable;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {


    private final AuditLogRepository auditRepository;


    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            Long entityId = extractId(result);
            String userId = getCurrentUser();

            AuditLog audit = AuditLog.builder()
                    .entityType(auditable.entity())
                    .entityId(entityId)
                    .action(auditable.action())
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditRepository.save(audit);

        } catch (Exception e) {
            log.warn("Audit failed: {}", e.getMessage());
        }

        return result;
    }

    private Long extractId(Object result) {
        if (result instanceof DriverDto dto) return dto.getId();
        if (result instanceof VehicleDto dto) return dto.getId();
        return null;
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}