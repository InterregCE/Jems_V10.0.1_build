ALTER TABLE payment
    DROP COLUMN programme_lump_sum_id,

    ADD COLUMN project_custom_identifier VARCHAR(31)      NOT NULL DEFAULT '',
    ADD COLUMN project_acronym           VARCHAR(25)      NOT NULL DEFAULT '',
    ADD COLUMN project_lump_sum_id       INT UNSIGNED     NOT NULL DEFAULT 0;
-- all values are updated by follow-up migration

UPDATE payment
    LEFT JOIN project proj on project_id = proj.id
SET project_lump_sum_id = project_id,
    project_custom_identifier = proj.custom_identifier,
    project_acronym = proj.acronym;

ALTER TABLE payment
    ADD CONSTRAINT fk_payment_to_project_lump_sum FOREIGN KEY (project_lump_sum_id, order_nr) REFERENCES project_lump_sum (project_id, order_nr)
        ON DELETE CASCADE
        ON UPDATE RESTRICT;

