CREATE TABLE project_description_c2_relevance_spf_recipient
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT UNSIGNED           NOT NULL,
    recipient_group         VARCHAR(127)           NOT NULL,
    sort_number          INT                    NOT NULL    DEFAULT 1,
    CONSTRAINT fk_project_spf_recipient_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_spf_recipient
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_spf_recipient_transl
(
    source_entity_id  BINARY(16) NOT NULL,
    language      VARCHAR(3) NOT NULL,
    specification TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_description_c2_relevance_spf_recipient_transl FOREIGN KEY (source_entity_id)
        REFERENCES project_description_c2_relevance_spf_recipient (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_spf_recipient_transl
    ADD SYSTEM VERSIONING;
