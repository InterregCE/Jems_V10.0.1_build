-- add programme_priority_transl

ALTER TABLE programme_priority
    DROP COLUMN title;

CREATE TABLE programme_priority_transl
(
    programme_priority_id INT UNSIGNED NOT NULL,
    language        VARCHAR(3)   NOT NULL,
    title     TEXT(300) DEFAULT NULL,
    PRIMARY KEY (programme_priority_id, language),
    CONSTRAINT fk_programme_priority_transl_to_programme_priority
        FOREIGN KEY (programme_priority_id)
            REFERENCES programme_priority (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
)
