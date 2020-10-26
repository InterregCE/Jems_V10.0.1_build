CREATE TABLE project_description_c2_relevance_transl
(
    project_id                INT UNSIGNED NOT NULL,
    language                  VARCHAR(3) NOT NULL,
    territorial_challenge     TEXT(5000) DEFAULT NULL,
    common_challenge          TEXT(5000) DEFAULT NULL,
    transnational_cooperation TEXT(5000) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_description_c2_relevance_transl_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance
    DROP COLUMN territorial_challenge;
ALTER TABLE project_description_c2_relevance
    DROP COLUMN common_challenge;
ALTER TABLE project_description_c2_relevance
    DROP COLUMN transnational_cooperation;
