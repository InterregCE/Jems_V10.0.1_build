UPDATE project
    INNER JOIN project_status ON project.id = project_status.project_id
SET project.first_submission_step1_id = (SELECT project_status.id
                                         FROM project_status
                                         WHERE project_status.project_id = project.id
                                           AND project_status.status = 'STEP1_SUBMITTED')
WHERE project_status.status = 'STEP1_SUBMITTED';


-- for old calls the first_submission_id column had the value of STEP1_SUBMISSION_ID instead of null
UPDATE project
    INNER JOIN project_status ON project.project_status_id = project_status.id
SET project.first_submission_id = NULL
WHERE project.project_status_id IN (SELECT project_status.id
                                    FROM project_status
                                    WHERE status IN (
                                                     'STEP1_SUBMITTED',
                                                     'STEP1_ELIGIBLE',
                                                     'STEP1_INELIGIBLE',
                                                     'STEP1_APPROVED',
                                                     'STEP1_APPROVED_WITH_CONDITIONS',
                                                     'STEP1_NOT_APPROVED')
                                    group by project_id);
