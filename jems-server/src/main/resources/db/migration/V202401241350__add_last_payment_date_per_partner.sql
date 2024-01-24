ALTER TABLE project_contracting_monitoring
    ADD COLUMN closure_date DATE DEFAULT NULL AFTER start_date;

CREATE TABLE project_contracting_partner_payment_date
(
    partner_id        INT UNSIGNED PRIMARY KEY,
    last_payment_date DATE NOT NULL,
    CONSTRAINT fk_contracting_partner_payment_date_to_partner
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
)
