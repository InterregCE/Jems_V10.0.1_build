CREATE TABLE project_work_package_activity_partner
(
    work_package_id    INT UNSIGNED NOT NULL,
    activity_number    TINYINT UNSIGNED NOT NULL,
    project_partner_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (work_package_id, activity_number, partner_id),
    CONSTRAINT fk_project_wp_activity_partner_to_project_wp
        FOREIGN KEY (work_package_id, activity_number) REFERENCES project_work_package_activity (work_package_id, activity_number)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_partner_to_project_partner
        FOREIGN KEY (project_partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
);
