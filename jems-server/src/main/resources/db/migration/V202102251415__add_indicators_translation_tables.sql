ALTER TABLE programme_indicator_output
    DROP COLUMN name;
ALTER TABLE programme_indicator_output
    DROP COLUMN measurement_unit;

ALTER TABLE programme_indicator_result
    DROP COLUMN name;
ALTER TABLE programme_indicator_result
    DROP COLUMN measurement_unit;
ALTER TABLE programme_indicator_result
    DROP COLUMN source_of_data;

CREATE TABLE programme_indicator_output_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    name             TEXT(255) DEFAULT NULL,
    measurement_unit TEXT(255) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_indicator_output_transl_to_indicator_output FOREIGN KEY (source_entity_id) REFERENCES programme_indicator_output (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE programme_indicator_result_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    name             TEXT(255) DEFAULT NULL,
    measurement_unit TEXT(255) DEFAULT NULL,
    source_of_data   TEXT DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_indicator_result_transl_to_indicator_result FOREIGN KEY (source_entity_id) REFERENCES programme_indicator_result (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
