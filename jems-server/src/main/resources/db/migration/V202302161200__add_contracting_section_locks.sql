CREATE TABLE lock_project_contracting_section(
    project_id INT UNSIGNED NOT NULL,
    section ENUM (
         'ContractsAgreements',
         'ProjectManagers',
         'ProjectReportingSchedule') NOT NULL,
     PRIMARY KEY (project_id, section),
     CONSTRAINT fk_project_contracting_section_lock_to_project FOREIGN KEY (project_id) REFERENCES project (id)
         ON DELETE CASCADE
         ON UPDATE RESTRICT
);


CREATE TABLE lock_project_contracting_partner(
     partner_id INT UNSIGNED PRIMARY KEY,
     project_id INT UNSIGNED NOT NULL,
     CONSTRAINT fk_project_contracting_partner_lock_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
         ON DELETE CASCADE
         ON UPDATE RESTRICT,
     CONSTRAINT fk_project_contracting_partner_lock_to_project FOREIGN KEY (project_id) REFERENCES project (id)
         ON DELETE CASCADE
         ON UPDATE RESTRICT
);
