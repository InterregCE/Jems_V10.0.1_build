
CREATE TABLE optimization_project_version (
    project_id  INT UNSIGNED PRIMARY KEY,
    last_approved_version DATETIME(6) NOT NULL,
    CONSTRAINT fk_optimization_project_version_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT IGNORE INTO optimization_project_version (project_id, last_approved_version)
SELECT p.id AS project_id, IF(ps.status IN ('APPROVED', 'CONTRACTED'), localtimestamp(6), MAX(pv.row_end)) AS timestamp
FROM project p
INNER JOIN project_status ps
    ON p.project_status_id = ps.id
INNER JOIN project_version pv
    ON p.id = pv.project_id
WHERE pv.row_end IS NOT NULL OR ps.status IN ('APPROVED', 'CONTRACTED')
GROUP BY p.id;
