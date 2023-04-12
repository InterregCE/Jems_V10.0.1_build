SET @@system_versioning_alter_history = 1;

ALTER TABLE project ADD COLUMN contracted_on_date DATE DEFAULT NULL;
