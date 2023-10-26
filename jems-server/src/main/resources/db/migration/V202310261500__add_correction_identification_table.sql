CREATE TABLE project_audit_correction_identification
(
    correction_id                   INT UNSIGNED NOT NULL PRIMARY KEY,
    follow_up_of_correction_id      INT UNSIGNED DEFAULT NULL,
    correction_follow_up_type        ENUM('No', 'LateRePayment', 'Interest', 'CourtProcedure') DEFAULT 'No',
    repayment_from                  DATETIME(3) DEFAULT NULL,
    late_repayment_to               DATETIME(3) DEFAULT NULL,
    partner_id                      INT UNSIGNED,
    partner_report_id               INT UNSIGNED,
    programme_fund_id                         INT UNSIGNED,

    CONSTRAINT fk_project_correction_identification_to_project_audit_correction FOREIGN KEY (correction_id) REFERENCES project_audit_correction (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_correction_followup_to_project_audit_correction FOREIGN KEY (follow_up_of_correction_id) REFERENCES project_audit_correction (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_correction_identification_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_correction_identification_to_report_project_partner FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_correction_identification_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
)
