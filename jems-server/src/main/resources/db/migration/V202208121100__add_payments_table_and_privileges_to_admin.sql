SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (@id, 'PaymentsRetrieve'), (@id, 'PaymentsUpdate');

CREATE TABLE payment
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id               INT UNSIGNED NOT NULL,
    partner_id               INT UNSIGNED NOT NULL,
    order_nr                 TINYINT UNSIGNED NOT NULL,
    programme_lump_sum_id    INT UNSIGNED NOT NULL,
    programme_fund_id        INT UNSIGNED NOT NULL,
    amount_approved_per_fund DECIMAL(11, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_payments_to_projects_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payments_to_project_lump_sum
        FOREIGN KEY (project_id, order_nr) REFERENCES project_lump_sum (project_id, order_nr)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_payments_to_project_partner
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_payments_to_projects_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);
