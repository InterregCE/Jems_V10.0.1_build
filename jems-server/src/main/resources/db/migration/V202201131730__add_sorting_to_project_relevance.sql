SET @@system_versioning_alter_history = 1;

ALTER TABLE project_description_c2_relevance_benefit_transl
    CHANGE COLUMN reference_id source_entity_id BINARY(16) NOT NULL;
ALTER TABLE project_description_c2_relevance_benefit
    ADD COLUMN sort_number INT NOT NULL DEFAULT 1;

ALTER TABLE project_description_c2_relevance_strategy_transl
    CHANGE COLUMN reference_id source_entity_id BINARY(16) NOT NULL;
ALTER TABLE project_description_c2_relevance_strategy
    ADD COLUMN sort_number INT NOT NULL DEFAULT 1;

ALTER TABLE project_description_c2_relevance_synergy_transl
    CHANGE COLUMN reference_id source_entity_id BINARY(16) NOT NULL;
ALTER TABLE project_description_c2_relevance_synergy
    ADD COLUMN sort_number INT NOT NULL DEFAULT 1;
