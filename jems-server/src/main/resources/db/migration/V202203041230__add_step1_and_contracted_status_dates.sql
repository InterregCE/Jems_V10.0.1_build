SET @@system_versioning_alter_history = 1;

ALTER TABLE project
    ADD COLUMN first_submission_step1_id  INT UNSIGNED DEFAULT NULL AFTER project_status_id,
    ADD COLUMN contracted_decision_id INT UNSIGNED DEFAULT NULL AFTER modification_decision_id,

ADD CONSTRAINT fk_project_step1_first_submission_project_status
	FOREIGN KEY(first_submission_step1_id) REFERENCES project_status (id)
		ON DELETE RESTRICT
		ON UPDATE RESTRICT,

ADD CONSTRAINT fk_contracted_decision_to_project_status
    FOREIGN KEY(contracted_decision_id) REFERENCES project_status (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;


UPDATE project
    INNER JOIN project_status ON project.project_status_id = project_status.id
    SET project.first_submission_step1_id  = project_status.id
    WHERE project_status.status = 'STEP1_SUBMITTED';

UPDATE project
    INNER JOIN project_status ON project.id = project_status.project_id
    SET project.contracted_decision_id  = (SELECT MIN(project_status.id) from project_status WHERE project_status.project_id = project.id AND project_status.status = 'CONTRACTED')
    WHERE project_status.status = 'CONTRACTED';

