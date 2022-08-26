ALTER TABLE project_contracting_reporting
    CHANGE COLUMN period_number period_number SMALLINT UNSIGNED,
    CHANGE COLUMN deadline deadline DATE;
