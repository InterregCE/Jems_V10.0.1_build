/*
 moves different texts of the following 6 tables into own translation tables
   - project_description_c1_overall_objective
   - project_description_c2_relevance
   - project_description_c2_relevance_benefit
   - project_description_c2_relevance_strategy
   - project_description_c2_relevance_synergy
   - project_description_c3_partnership
*/

-- project_description_c1_overall_objective
CREATE TABLE project_description_c1_overall_objective_transl
(
    project_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    overall_objective       TEXT(500) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_description_c1_overall_objective_transl FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c1_overall_objective
    DROP COLUMN project_overall_objective;

-- project_description_c2_relevance
ALTER TABLE project_description_c2_relevance_transl
    ADD COLUMN    available_knowledge       TEXT(5000) DEFAULT NULL;

ALTER TABLE project_description_c2_relevance
    DROP COLUMN available_knowledge;

-- project_description_c2_relevance_benefit
CREATE TABLE project_description_c2_relevance_benefit_transl
(
    reference_id                BINARY(16) NOT NULL,
    language                    VARCHAR(3) NOT NULL,
    specification               TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (reference_id, language),
    CONSTRAINT fk_project_description_c2_relevance_benefit_transl FOREIGN KEY (reference_id)
        REFERENCES project_description_c2_relevance_benefit (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_benefit
    DROP COLUMN specification;

-- project_description_c2_relevance_strategy
CREATE TABLE project_description_c2_relevance_strategy_transl
(
    reference_id                BINARY(16) NOT NULL,
    language                    VARCHAR(3) NOT NULL,
    specification               TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (reference_id, language),
    CONSTRAINT fk_project_description_c2_relevance_strategy_transl FOREIGN KEY (reference_id)
        REFERENCES project_description_c2_relevance_strategy (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_strategy
    DROP COLUMN specification;

-- project_description_c2_relevance_synergy
CREATE TABLE project_description_c2_relevance_synergy_transl
(
    reference_id                BINARY(16) NOT NULL,
    language                    VARCHAR(3) NOT NULL,
    synergy                     VARCHAR(500) DEFAULT NULL,
    specification               TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (reference_id, language),
    CONSTRAINT fk_project_description_c2_relevance_synergy_transl FOREIGN KEY (reference_id)
        REFERENCES project_description_c2_relevance_synergy (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_synergy
    DROP COLUMN synergy;
ALTER TABLE project_description_c2_relevance_synergy
    DROP COLUMN specification;

-- project_description_c3_partnership
CREATE TABLE project_description_c3_partnership_transl
(
    project_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    project_partnership     TEXT(500) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_description_c3_partnership_transl FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c3_partnership
    DROP COLUMN project_partnership;
