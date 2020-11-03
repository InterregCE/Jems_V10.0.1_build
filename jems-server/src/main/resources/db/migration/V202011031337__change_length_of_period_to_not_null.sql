-- fix until the next merge of migration scripts
UPDATE project_call SET length_of_period = 12
    WHERE length_of_period is NULL;
-- end-of-fix

ALTER TABLE project_call
    MODIFY length_of_period INT NOT NULL;
