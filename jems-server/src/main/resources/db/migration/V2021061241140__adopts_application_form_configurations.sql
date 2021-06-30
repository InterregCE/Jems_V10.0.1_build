DELETE
FROM application_form_field_configuration
WHERE application_form_configuration_id = 1;

ALTER TABLE application_form_field_configuration
    DROP CONSTRAINT fk_field_to_form_configuration;

DROP TABLE application_form_configuration;

ALTER TABLE application_form_field_configuration
    CHANGE COLUMN application_form_configuration_id call_id INT UNSIGNED,
    ADD CONSTRAINT fk_to_call_entity
        FOREIGN KEY (call_id) REFERENCES project_call (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
