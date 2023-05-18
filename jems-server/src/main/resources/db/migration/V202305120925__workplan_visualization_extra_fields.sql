ALTER TABLE report_project_wp
    ADD COLUMN previous_specific_status           ENUM ('Fully', 'Partly', 'Not') DEFAULT NULL AFTER specific_status,
    ADD COLUMN previous_communication_status      ENUM ('Fully', 'Partly', 'Not') DEFAULT NULL AFTER communication_status,
    ADD COLUMN previous_completed                 BOOLEAN      NOT NULL           DEFAULT FALSE AFTER completed;

ALTER TABLE report_project_wp_transl
    ADD COLUMN previous_specific_explanation      TEXT(2000)   NOT NULL DEFAULT '' AFTER specific_explanation,
    ADD COLUMN previous_communication_explanation TEXT(2000)   NOT NULL DEFAULT '' AFTER communication_explanation;
    ADD COLUMN previous_description TEXT(2000)   NOT NULL DEFAULT '' AFTER description;

ALTER TABLE report_project_wp_activity_deliverable
    ADD COLUMN previous_current_report      DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_report;

ALTER TABLE report_project_wp_activity_deliverable_transl
    ADD COLUMN previous_progress         TEXT(2000)   NOT NULL DEFAULT '' AFTER progress;

ALTER TABLE report_project_wp_activity
    ADD COLUMN previous_status                 ENUM ('Fully', 'Partly', 'Not') DEFAULT NULL AFTER status;

ALTER TABLE report_project_wp_activity_transl
    ADD COLUMN previous_progress         TEXT(2000)   NOT NULL DEFAULT '' AFTER progress;

ALTER TABLE report_project_wp_investment_transl
    ADD COLUMN previous_progress                              TEXT(2000)   NOT NULL DEFAULT '' AFTER progress;

ALTER TABLE report_project_wp_output
    ADD COLUMN previous_current_report         DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_report;

ALTER TABLE report_project_wp_output_transl
    ADD COLUMN previous_progress         TEXT(2000)   NOT NULL DEFAULT '' AFTER progress;

