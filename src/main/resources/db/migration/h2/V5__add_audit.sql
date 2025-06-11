CREATE SEQUENCE audit_logs_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE audit_logs (
                            id BIGINT DEFAULT NEXTVAL('audit_logs_seq') PRIMARY KEY,
                            entity_type VARCHAR(50) NOT NULL,
                            entity_id BIGINT,
                            action VARCHAR(50) NOT NULL,
                            user_id VARCHAR(100) NOT NULL,
                            timestamp TIMESTAMP NOT NULL,
                            changes TEXT
);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);