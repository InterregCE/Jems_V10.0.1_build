CREATE TABLE checklist_instance
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    status                 VARCHAR(50) NOT NULL,
    finished_date          DATE DEFAULT NULL,
    related_to_id          INT UNSIGNED NOT NULL,
    programme_checklist_id INT UNSIGNED NOT NULL,
    creator_id             INT UNSIGNED NOT NULL,

    CONSTRAINT fk_checklist_instance_to_programme_checklist
        FOREIGN KEY (programme_checklist_id) REFERENCES programme_checklist(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_checklist_instance_to_creator
        FOREIGN KEY (creator_id) REFERENCES account(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE checklist_component_instance
(
    programme_component_id INT UNSIGNED NOT NULL,
    checklist_instance_id  INT UNSIGNED NOT NULL,
    metadata               JSON,

    PRIMARY KEY (programme_component_id, checklist_instance_id),

    CHECK (JSON_VALID(metadata)),
    CONSTRAINT fk_checklist_component_instance_to_checklist_instance
        FOREIGN KEY (checklist_instance_id) REFERENCES checklist_instance (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_checklist_component_instance_to_programme_checklist_component
        FOREIGN KEY (programme_component_id) REFERENCES programme_checklist_component (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

UPDATE programme_checklist
SET type = 'APPLICATION_FORM_ASSESSMENT'
WHERE type = 'ELIGIBILITY';

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProjectAssessmentChecklistUpdate'
FROM account_role as accountRole
WHERE accountRole.name = 'administrator'
ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProjectAssessmentChecklistUpdate'
FROM account_role as accountRole
WHERE accountRole.name = 'programme user'
ORDER BY id DESC LIMIT 1;
