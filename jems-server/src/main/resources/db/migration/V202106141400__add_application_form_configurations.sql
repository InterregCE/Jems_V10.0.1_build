CREATE TABLE application_form_configuration
(
    id   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE application_form_field_configuration
(
    id                                VARCHAR(255)                                     NOT NULL,
    application_form_configuration_id INT UNSIGNED                                     NOT NULL,
    visibility_status                 ENUM ('NONE','STEP_ONE_AND_TWO','STEP_TWO_ONLY') NOT NULL DEFAULT 'STEP_ONE_AND_TWO',
    PRIMARY KEY (id, application_form_configuration_id),
    CONSTRAINT fk_field_to_form_configuration
        FOREIGN KEY (application_form_configuration_id) REFERENCES application_form_configuration (id)
);

INSERT INTO application_form_configuration (id, name)
VALUES (1, 'Default');

INSERT INTO application_form_field_configuration (id, application_form_configuration_id, visibility_status)
VALUES ('project.application.form.field.project.acronym', 1, 'STEP_ONE_AND_TWO'),
       ('project.application.form.field.project.title', 1, 'STEP_ONE_AND_TWO'),
       ('project.application.form.field.project.duration', 1, 'STEP_ONE_AND_TWO'),
       ('project.application.form.field.project.priority', 1, 'STEP_ONE_AND_TWO'),
       ('project.application.form.field.project.objective', 1, 'STEP_ONE_AND_TWO'),
       ('project.application.form.field.project.summary', 1, 'STEP_ONE_AND_TWO'),
       ('project.results.result.indicator.and.measurement.unit', 1, 'STEP_ONE_AND_TWO'),
       ('project.results.result.target.value', 1, 'STEP_ONE_AND_TWO'),
       ('project.results.result.delivery.period', 1, 'STEP_ONE_AND_TWO'),
       ('project.results.result.description', 1, 'STEP_ONE_AND_TWO')
