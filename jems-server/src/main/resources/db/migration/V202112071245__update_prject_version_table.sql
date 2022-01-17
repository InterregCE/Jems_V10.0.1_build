DELIMITER $$
CREATE OR REPLACE PROCEDURE insertNewVersion(projectId INT, toCreateVersion INT, accountId INT, createdAt DATETIME(3))
BEGIN
    INSERT INTO project_version(version, project_id, account_id, created_at, row_end)
    VALUES (CONCAT(CAST(toCreateVersion as CHAR), '.0'), projectId, accountId, createdAt, NULL);
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE updatePreviousVersionRowEnd(projectId INT, currentVersion INT, rowStart DATETIME(6))
BEGIN
    # rowStart is the timestamp for the start of the current version
    # we need to set the row_end of previous version to the maximum timestamp less than rowStart
    UPDATE project_version
    SET row_end = rowStart - 0.000001
    WHERE project_id = projectId
      AND version = currentVersion - 1;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE insertVersionsForProject(projectId INT)
BEGIN
    DECLARE done INT;
    DECLARE rowStart DATETIME(6);
    DECLARE status VARCHAR(127);
    DECLARE createdAt DATETIME(3);
    DECLARE statusId INT;
    DECLARE accountId INT;
    DECLARE toCreateVersion INT DEFAULT 1;
    DECLARE previousStatus VARCHAR(127) DEFAULT NULL;

    DECLARE projectStatuses CURSOR FOR
        SELECT p.project_status_id, ps.status, ps.account_id, ps.updated, p.row_start
        FROM project FOR SYSTEM_TIME ALL AS p
        LEFT JOIN project_status AS ps ON ps.id = p.project_status_id
        WHERE p.id = projectId AND ps.status IN ('STEP1_DRAFT', 'DRAFT', 'RETURNED_TO_APPLICANT', 'RETURNED_TO_APPLICANT_FOR_CONDITIONS' , 'CONDITIONS_SUBMITTED', 'MODIFICATION_PRECONTRACTING', 'MODIFICATION_PRECONTRACTING_SUBMITTED', 'APPROVED')
        GROUP BY p.project_status_id
        ORDER BY row_end;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN projectStatuses;

    insertVersions:
    LOOP
        FETCH projectStatuses INTO statusId, status, accountId, createdAt, rowStart;
        IF done = 1 THEN
            LEAVE insertVersions;
        END IF;

        IF (status = 'STEP1_DRAFT' OR status = 'DRAFT' OR status = 'RETURNED_TO_APPLICANT' OR
            (status = 'RETURNED_TO_APPLICANT_FOR_CONDITIONS' AND previousStatus != 'CONDITIONS_SUBMITTED') OR
            (status = 'MODIFICATION_PRECONTRACTING' AND previousStatus != 'MODIFICATION_PRECONTRACTING_SUBMITTED')) THEN
            CALL insertNewVersion(projectId, toCreateVersion, accountId, createdAt);
            CALL updatePreviousVersionRowEnd(projectId, toCreateVersion, rowStart);
            SET toCreateVersion = toCreateVersion + 1;
        END IF;

        SET previousStatus = status;
    END LOOP insertVersions;
    CLOSE projectStatuses;

    # Considering the fact that we haven't had rejections so far
    # here we are setting the last version as current version by setting it's row_end to null
    UPDATE project_version
    SET row_end = NULL
    WHERE project_id = projectId
    ORDER BY version DESC
    LIMIT 1;

END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE insertVersionsForAllProjects()
BEGIN

    DECLARE done INT;
    DECLARE projectId INT;

    DECLARE projects CURSOR FOR
        SELECT id FROM project;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN projects;

    insertVersionsForProjects:
    LOOP
        FETCH projects INTO projectId;
        IF done = 1 THEN
            LEAVE insertVersionsForProjects;
        END IF;
        CALL insertVersionsForProject(projectId);
    END LOOP insertVersionsForProjects;
    CLOSE projects;
END $$
DELIMITER ;

ALTER TABLE project_version
    MODIFY row_end DATETIME(6) INVISIBLE DEFAULT NULL,
    DROP IF EXISTS status;

TRUNCATE project_version;

CALL insertVersionsForAllProjects();

-- remove the stored procedures
DROP PROCEDURE insertNewVersion;
DROP PROCEDURE updatePreviousVersionRowEnd;
DROP PROCEDURE insertVersionsForProject;
DROP PROCEDURE insertVersionsForAllProjects;
