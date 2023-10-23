CREATE TABLE project_audit_correction
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    audit_control_id  INT UNSIGNED NOT NULL,
    order_nr          TINYINT UNSIGNED NOT NULL,
    status            ENUM('Ongoing', 'Closed') NOT NULL DEFAULT 'Ongoing',
    linked_to_invoice BOOLEAN NOT NULL,

    CONSTRAINT fk_project_correction_to_project_audit_control FOREIGN KEY (audit_control_id) REFERENCES project_audit_control (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
)
