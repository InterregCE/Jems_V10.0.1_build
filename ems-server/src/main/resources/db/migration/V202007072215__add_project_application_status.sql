CREATE TABLE project_status
(
    id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    project_id INTEGER,
    status VARCHAR(127) NOT NULL,
    account_id INTEGER NOT NULL,
    updated DATETIME NOT NULL DEFAULT NOW(),
    note VARCHAR(255),
    CONSTRAINT fk_project_status_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_status_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

ALTER TABLE project
    ADD COLUMN project_status_id INTEGER NOT NULL AFTER submission_date,
    ADD CONSTRAINT fk_project_project_status
        FOREIGN KEY (project_status_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

ALTER TABLE project MODIFY submission_date DATETIME NULL;

DELIMITER $$

CREATE TRIGGER protect_resetting_project_from_status
    BEFORE UPDATE
    ON project_status
    FOR EACH ROW
BEGIN
    IF NEW.project_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'project cannot be removed from the status, it can be null only when created initially';
    END IF;
END$$

DELIMITER ;

