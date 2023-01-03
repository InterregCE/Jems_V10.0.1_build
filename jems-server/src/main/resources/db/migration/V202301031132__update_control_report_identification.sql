UPDATE report_project_partner_designated_controller
    SET telephone = NULL;

ALTER TABLE report_project_partner_designated_controller
    MODIFY COLUMN telephone VARCHAR(25) DEFAULT NULL;
