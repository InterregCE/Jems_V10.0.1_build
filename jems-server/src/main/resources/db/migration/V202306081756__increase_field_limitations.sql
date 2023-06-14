SET @@system_versioning_alter_history = 1;

ALTER TABLE report_project_partner_identification_transl
    MODIFY COLUMN summary TEXT(5000) DEFAULT NULL,
    MODIFY COLUMN problems_and_deviations TEXT(5000) DEFAULT NULL,
    MODIFY COLUMN spending_deviations TEXT(5000) DEFAULT NULL;

ALTER TABLE report_project_partner_wp_transl
    MODIFY COLUMN description TEXT(5000) DEFAULT NULL;

ALTER TABLE project_partner
    MODIFY COLUMN name_in_original_language VARCHAR(250) DEFAULT NULL,
    MODIFY COLUMN name_in_english VARCHAR(250) DEFAULT NULL;

ALTER TABLE project_associated_organization
    MODIFY COLUMN name_in_original_language VARCHAR(250) DEFAULT NULL,
    MODIFY COLUMN name_in_english VARCHAR(250) DEFAULT NULL;

ALTER TABLE report_project_partner
    MODIFY COLUMN name_in_original_language VARCHAR(250) DEFAULT NULL,
    MODIFY COLUMN name_in_english VARCHAR(250) DEFAULT NULL;
