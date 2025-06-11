package org.example.driverandfleetmanagementapp.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {


    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);
}