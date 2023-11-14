DELETE FROM project_audit_correction;

ALTER TABLE project_audit_correction
    DROP COLUMN linked_to_invoice,
    ADD COLUMN correction_type              ENUM ('LinkedToInvoice', 'LinkedToCostOption') DEFAULT NULL,
    ADD COLUMN follow_up_of_correction_id   INT UNSIGNED                                   DEFAULT NULL,
    ADD COLUMN follow_up_of_correction_type ENUM ('No', 'LateRePayment', 'Interest', 'CourtProcedure') NOT NULL DEFAULT 'No',
    ADD COLUMN repayment_date               DATE                                           DEFAULT NULL,
    ADD COLUMN late_repayment               DATE                                           DEFAULT NULL,
    ADD COLUMN partner_report_id            INT UNSIGNED,
    ADD COLUMN programme_fund_id            INT UNSIGNED,
    ADD CONSTRAINT fk_audit_correction_to_follow_up_correction
        FOREIGN KEY (follow_up_of_correction_id) REFERENCES project_audit_correction (id),
    ADD CONSTRAINT fk_audit_correction_to_report
        FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id),
    ADD CONSTRAINT fk_audit_correction_to_fund
        FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id);

ALTER TABLE project_audit_correction
    RENAME TO audit_control_correction;


DROP TABLE project_audit_correction_identification;


ALTER TABLE project_audit_control
    DROP COLUMN project_custom_identifier;

ALTER TABLE project_audit_control
    RENAME TO audit_control;


ALTER TABLE project_audit_correction_financial_description
    RENAME TO audit_control_correction_finance;

ALTER TABLE project_audit_correction_programme_measure
    RENAME TO audit_control_correction_measure;
