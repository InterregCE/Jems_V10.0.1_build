CREATE TABLE project_partner_state_aid
(
    partner_id INT UNSIGNED PRIMARY KEY,
    answer1    BOOLEAN DEFAULT NULL,
    answer2    BOOLEAN DEFAULT NULL,
    answer3    BOOLEAN DEFAULT NULL,
    answer4    BOOLEAN DEFAULT NULL,
    CONSTRAINT fk_project_partner_state_aid_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_partner_state_aid_transl
(
    partner_id     INT UNSIGNED NOT NULL,
    language       VARCHAR(3) NOT NULL,
    justification1 TEXT DEFAULT NULL,
    justification2 TEXT DEFAULT NULL,
    justification3 TEXT DEFAULT NULL,
    justification4 TEXT DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_state_aid_transl_to_partner_state_aid FOREIGN KEY (partner_id) REFERENCES project_partner_state_aid (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_state_aid_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_partner_state_aid
    ADD SYSTEM VERSIONING;
