UPDATE project_status ps
SET ps.status = 'RETURNED_TO_APPLICANT_FOR_CONDITIONS'
WHERE ps.id IN (
    WITH latest_status AS (
        SELECT statuses.*, ROW_NUMBER() OVER (PARTITION BY project_id ORDER BY id DESC) AS rownumber
        FROM project_status AS statuses
    )
    SELECT latest_status.id
    FROM latest_status
             INNER JOIN (SELECT * FROM latest_status where rownumber = 2) as penultimate_status
                        on penultimate_status.project_id = latest_status.project_id
    WHERE (latest_status.rownumber = 1 AND penultimate_status.status = 'APPROVED_WITH_CONDITIONS' AND
           latest_status.status = 'RETURNED_TO_APPLICANT'));
