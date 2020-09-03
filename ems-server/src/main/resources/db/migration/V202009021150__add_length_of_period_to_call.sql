ALTER TABLE project_call
    ADD COLUMN length_of_period INTEGER DEFAULT NULL AFTER end_date;
