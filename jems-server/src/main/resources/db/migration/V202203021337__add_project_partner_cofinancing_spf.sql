CREATE TABLE project_partner_co_financing_spf
(
    partner_id        INT UNSIGNED NOT NULL,
    order_nr          TINYINT UNSIGNED NOT NULL,
    percentage        DECIMAL(11, 2) NOT NULL,
    programme_fund_id INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (partner_id, order_nr),
    CONSTRAINT fk_project_partner_co_financing_spf_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_co_financing_spf_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_co_financing_spf
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_contribution_spf
(
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    partner_id INT UNSIGNED   NOT NULL,
    name       VARCHAR(255)   DEFAULT NULL,
    status     ENUM ('Private', 'Public', 'AutomaticPublic') DEFAULT NULL,
    amount     DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_project_partner_contribution_spf_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_contribution_spf
    ADD SYSTEM VERSIONING;
