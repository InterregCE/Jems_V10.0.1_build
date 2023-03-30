ALTER TABLE project_contracting_reporting
    ADD COLUMN number SMALLINT NOT NULL DEFAULT 1 AFTER project_id;

UPDATE project_contracting_reporting pcr
    INNER JOIN(
    SELECT id, DENSE_RANK() OVER (PARTITION BY project_id ORDER BY id ASC) as position
    FROM project_contracting_reporting
    ) rank_table
ON pcr.id = rank_table.id
    SET pcr.number = rank_table.position;



