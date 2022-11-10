INSERT IGNORE INTO account_role_permission(account_role_id, permission)
    SELECT accountRole.id, 'AdvancePaymentsRetrieve' FROM account_role as accountRole
        WHERE accountRole.name = 'administrator'
    ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
    SELECT accountRole.id, 'AdvancePaymentsUpdate' FROM account_role as accountRole
        WHERE accountRole.name = 'administrator'
    ORDER BY id DESC LIMIT 1;

CREATE TABLE payment_advance
(
    id                           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id                   INT UNSIGNED NOT NULL,
    project_version              VARCHAR(127) NOT NULL,
    project_custom_identifier    VARCHAR(31)  NOT NULL,
    project_acronym              VARCHAR(25)  DEFAULT NULL,
    partner_id                   INT UNSIGNED NOT NULL,
    partner_abbreviation         VARCHAR(15)  DEFAULT NULL,
    partner_role                 VARCHAR(127) DEFAULT NULL,
    partner_sort_number          INT          DEFAULT NULL,
    programme_fund_id            INT UNSIGNED DEFAULT NULL,
    partner_contribution_id         INT UNSIGNED DEFAULT NULL,
    partner_contribution_name       VARCHAR(255) DEFAULT NULL,
    partner_contribution_spf_id     INT UNSIGNED DEFAULT NULL,
    partner_contribution_spf_name   VARCHAR(255) DEFAULT NULL,
    amount_paid                  DECIMAL(11, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    payment_date                 DATETIME(3) DEFAULT NULL,
    comment                      TEXT(500),
    is_payment_authorized_info          BOOLEAN,
    payment_authorized_info_account_id  INT UNSIGNED,
    payment_authorized_date             DATETIME(3)  DEFAULT NULL,
    is_payment_confirmed                BOOLEAN,
    payment_confirmed_account_id        INT UNSIGNED,
    payment_confirmed_date              DATETIME(3)  DEFAULT NULL,
    CONSTRAINT fk_payment_adv_to_projects_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_adv_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_adv_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);
