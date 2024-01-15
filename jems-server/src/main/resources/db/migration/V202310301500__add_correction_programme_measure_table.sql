CREATE TABLE project_audit_correction_programme_measure
(
    correction_id INT UNSIGNED NOT NULL PRIMARY KEY,
    scenario      ENUM('NA', 'SCENARIO_1', 'SCENARIO_2', 'SCENARIO_3', 'SCENARIO_4', 'SCENARIO_5', 'SCENARIO_6') DEFAULT 'NA',
    comment       VARCHAR(2000) DEFAULT NULL,

    CONSTRAINT fk_project_correction_prog_measure_to_project_audit_correction FOREIGN KEY (correction_id) REFERENCES project_audit_correction (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
)
