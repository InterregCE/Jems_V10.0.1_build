CREATE TABLE report_project_closure_project_story
(
    report_id                   INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_closure_project_story_to_report_project FOREIGN KEY (report_id) REFERENCES report_project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE report_project_closure_project_story_transl
(
    source_entity_id            INT UNSIGNED NOT NULL,
    language                    VARCHAR(3) NOT NULL,
    story                       TEXT(5000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_closure_project_story_transl FOREIGN KEY (source_entity_id)
        REFERENCES report_project_closure_project_story (report_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE report_project_closure_project_prize
(
    id                          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id                   INT UNSIGNED           NOT NULL,
    sort_number                 INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_closure_project_prize_to_report_project FOREIGN KEY (report_id) REFERENCES report_project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE report_project_closure_project_prize_transl
(
    source_entity_id            INT UNSIGNED NOT NULL,
    language                    VARCHAR(3) NOT NULL,
    prize                       TEXT(500) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_closure_project_prize_transl FOREIGN KEY (source_entity_id)
        REFERENCES report_project_closure_project_prize (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
