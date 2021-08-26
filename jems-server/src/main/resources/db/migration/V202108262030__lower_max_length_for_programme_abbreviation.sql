UPDATE programme_data
    SET project_id_programme_abbreviation=SUBSTRING(project_id_programme_abbreviation, 1, 12);

ALTER TABLE programme_data
    MODIFY COLUMN project_id_programme_abbreviation VARCHAR(12) DEFAULT NULL;
