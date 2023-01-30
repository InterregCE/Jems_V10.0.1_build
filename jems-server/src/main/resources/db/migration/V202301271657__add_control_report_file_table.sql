CREATE TABLE report_project_partner_control_file
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id         INT UNSIGNED NOT NULL,
    generated_file_id INT UNSIGNED NOT NULL,
    signed_file_id    INT UNSIGNED DEFAULT NULL,

    CONSTRAINT fk_report_control_generated_file_to_file_metadata
        FOREIGN KEY(generated_file_id) REFERENCES file_metadata(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_report_control_signed_file_to_file_metadata
        FOREIGN KEY(signed_file_id) REFERENCES file_metadata(id)
         ON DELETE CASCADE
         ON UPDATE RESTRICT
);
