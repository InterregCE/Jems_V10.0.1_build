ALTER TABLE programme_legal_status
    ADD COLUMN type ENUM ('Private','Public','Other') NOT NULL DEFAULT 'Other';

ALTER TABLE programme_legal_status_transl
    CHANGE COLUMN `legal_status_id`
        source_entity_id INT UNSIGNED NOT NULL;

UPDATE programme_legal_status
SET type='Public'
WHERE id = (SELECT source_entity_id from programme_legal_status_transl WHERE language = 'EN' AND description = 'Public' ORDER BY id LIMIT 1);

UPDATE programme_legal_status
SET type='Private'
WHERE id = (SELECT source_entity_id from programme_legal_status_transl WHERE language = 'EN' AND description = 'Private'  ORDER BY id LIMIT 1);

ALTER TABLE programme_legal_status
    DROP description;
