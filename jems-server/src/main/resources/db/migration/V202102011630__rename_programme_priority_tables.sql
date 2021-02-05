# Remove checks for correct association between specific objective policy and programme objective
# and handle this only in application
DROP TRIGGER priority_policy_should_have_correct_programme_objective_insert;
DROP TRIGGER priority_policy_should_have_correct_programme_objective_update;

# Remove UNIQUE constraint on code and handle this only in application, otherwise batch update is not possible
ALTER TABLE programme_priority_policy DROP INDEX code;

RENAME TABLE programme_priority_policy TO programme_priority_specific_objective;
RENAME TABLE project_call_priority_policy TO project_call_priority_specific_objective;

ALTER TABLE project_call_priority_specific_objective
    CHANGE COLUMN programme_priority_policy programme_specific_objective VARCHAR(127) NOT NULL;
