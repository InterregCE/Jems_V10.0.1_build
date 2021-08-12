DROP TABLE project_partner_co_financing;

CREATE TABLE project_partner_co_financing
(
    partner_id        INT UNSIGNED NOT NULL,
    order_nr          TINYINT UNSIGNED NOT NULL,
    percentage        DECIMAL(11, 2) NOT NULL,
    programme_fund_id INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (partner_id, order_nr),
    CONSTRAINT fk_project_partner_co_financing_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_co_financing_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_co_financing
    ADD SYSTEM VERSIONING;
