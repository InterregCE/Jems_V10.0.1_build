ALTER TABLE payment
    ADD CONSTRAINT fk_payment_project_report_to_project_report FOREIGN KEY (project_report_id) REFERENCES report_project (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;