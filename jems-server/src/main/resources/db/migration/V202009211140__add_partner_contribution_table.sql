CREATE TABLE project_partner_contribution
(
    partner_id               INT UNSIGNED PRIMARY KEY,
    organization_relevance   TEXT         DEFAULT NULL,
    organization_role        TEXT         DEFAULT NULL,
    organization_experience  TEXT         DEFAULT NULL,
    CONSTRAINT fk_partner_contribution_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
