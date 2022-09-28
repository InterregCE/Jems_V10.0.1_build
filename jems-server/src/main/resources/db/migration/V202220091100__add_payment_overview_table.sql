DROP TABLE payment;

CREATE TABLE payment
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    type                     ENUM ('FTLS', 'REGULAR') NOT NULL,
    project_id               INT UNSIGNED NOT NULL,
    order_nr                 TINYINT UNSIGNED NOT NULL,
    programme_lump_sum_id    INT UNSIGNED NOT NULL,
    programme_fund_id        INT UNSIGNED NOT NULL,
    amount_approved_per_fund DECIMAL(11, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_payment_detail_to_projects_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_detail_to_projects_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);

CREATE TABLE payment_partner
(
    id                          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_id                  INT UNSIGNED NOT NULL,
    partner_id                  INT UNSIGNED NOT NULL,
    amount_approved_per_partner DECIMAL(11, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_payment_detail_to_payment_overview FOREIGN KEY (payment_id) REFERENCES payment (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_detail_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
