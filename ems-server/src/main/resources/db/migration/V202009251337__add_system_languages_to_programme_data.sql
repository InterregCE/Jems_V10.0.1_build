ALTER TABLE programme_data
    ADD COLUMN languages_system VARCHAR(127) DEFAULT NULL AFTER programme_amending_decision_date;
