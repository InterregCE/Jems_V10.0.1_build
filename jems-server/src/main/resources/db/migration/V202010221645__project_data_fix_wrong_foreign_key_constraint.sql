DROP TABLE IF EXISTS project_data;

DELETE
FROM project
WHERE id IS NOT NULL;

ALTER TABLE project
    ADD COLUMN project_call_priority_policy_call_id                   INT UNSIGNED DEFAULT NULL AFTER project_call_id,
    ADD COLUMN project_call_priority_policy_programme_priority_policy VARCHAR(127) DEFAULT NULL AFTER project_call_priority_policy_call_id,
    ADD COLUMN title                                                  VARCHAR(255) DEFAULT NULL AFTER funding_decision_id,
    ADD COLUMN duration                                               INTEGER      DEFAULT NULL AFTER title,
    ADD COLUMN intro                                                  TEXT         DEFAULT NULL AFTER duration,
    ADD COLUMN intro_programme_language                               TEXT         DEFAULT NULL AFTER intro,
    ADD CONSTRAINT fk_project_to_project_call_priority_policy
        FOREIGN KEY (
                     project_call_priority_policy_call_id,
                     project_call_priority_policy_programme_priority_policy
            ) REFERENCES project_call_priority_policy (call_id, programme_priority_policy)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

DELIMITER $$

CREATE TRIGGER point_project_to_same_policy_as_call_insert
    BEFORE INSERT
    ON project
    FOR EACH ROW
BEGIN
    IF NEW.project_call_priority_policy_programme_priority_policy IS NOT NULL THEN
        SET NEW.project_call_priority_policy_call_id = NEW.project_call_id;
    ELSE
        SET NEW.project_call_priority_policy_call_id = NULL;
    END IF;
END$$

CREATE TRIGGER point_project_to_same_policy_as_call_update
    BEFORE UPDATE
    ON project
    FOR EACH ROW
BEGIN
    IF NEW.project_call_priority_policy_programme_priority_policy IS NOT NULL THEN
        SET NEW.project_call_priority_policy_call_id = NEW.project_call_id;
    ELSE
        SET NEW.project_call_priority_policy_call_id = NULL;
    END IF;
END$$

DELIMITER ;
