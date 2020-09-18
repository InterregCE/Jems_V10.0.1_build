CREATE TABLE project_description_c1_overall_objective
(
    project_id                INT PRIMARY KEY,
    project_overall_objective TEXT(500) DEFAULT NULL,
    CONSTRAINT fk_project_description_c1_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_description_c2_relevance
(
    project_id                INT PRIMARY KEY,
    territorial_challenge     TEXT(5000) DEFAULT NULL,
    common_challenge          TEXT(5000) DEFAULT NULL,
    transnational_cooperation TEXT(5000) DEFAULT NULL,
    available_knowledge       TEXT(5000) DEFAULT NULL,
    CONSTRAINT fk_project_description_c2_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_description_c2_relevance_benefit
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT                    NOT NULL,
    target_group         VARCHAR(127)           NOT NULL,
    specification        TEXT(2000) DEFAULT NULL,
    CONSTRAINT fk_project_benefit_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_description_c2_relevance_strategy
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT                    NOT NULL,
    strategy             VARCHAR(127)           NOT NULL,
    specification        TEXT(2000) DEFAULT NULL,
    CONSTRAINT fk_project_strategy_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_strategy_to_project_call_strategy FOREIGN KEY (strategy) REFERENCES programme_strategy (strategy)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_description_c2_relevance_synergy
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT                    NOT NULL,
    synergy              VARCHAR(500)           NOT NULL,
    specification        TEXT(2000) DEFAULT NULL,
    CONSTRAINT fk_project_synergy_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_description_c3_partnership
(
    project_id          INT PRIMARY KEY,
    project_partnership TEXT(5000) DEFAULT NULL,
    CONSTRAINT fk_project_description_c3_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
