ALTER TABLE payment
    DROP FOREIGN KEY fk_payment_to_project_lump_sum,
    MODIFY COLUMN project_lump_sum_id INT UNSIGNED NULL,
    MODIFY COLUMN order_nr TINYINT UNSIGNED NULL,
    ADD COLUMN project_report_id INT UNSIGNED NULL after project_lump_sum_id;


ALTER TABLE payment
    ADD CONSTRAINT fk_payment_to_project_lump_sum FOREIGN KEY (project_lump_sum_id, order_nr) REFERENCES project_lump_sum (project_id, order_nr)
        ON DELETE CASCADE
        ON UPDATE RESTRICT;


ALTER TABLE payment_partner
    ADD COLUMN partner_certificate_id INT UNSIGNED NULL after partner_id,
    ADD CONSTRAINT fk_payment_partner_to_report_project_partner FOREIGN KEY (partner_certificate_id) REFERENCES report_project_partner (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;