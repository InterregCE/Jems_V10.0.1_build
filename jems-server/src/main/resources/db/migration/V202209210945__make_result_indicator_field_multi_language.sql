ALTER TABLE programme_indicator_result_transl
    ADD COLUMN comment TEXT DEFAULT NULL;

INSERT INTO programme_indicator_result_transl (source_entity_id, language, comment)
    (SELECT programme_indicator_result.id, programme_language.code, programme_indicator_result.comment
    FROM programme_indicator_result INNER JOIN programme_language ON programme_language.ui = 1)
    ON DUPLICATE KEY UPDATE comment = programme_indicator_result.comment;

ALTER TABLE programme_indicator_result DROP COLUMN comment;
