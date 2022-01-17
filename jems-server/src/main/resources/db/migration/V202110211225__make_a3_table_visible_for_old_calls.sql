INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.section.a.3', id, 'STEP_TWO_ONLY'
FROM project_call
WHERE id not in (
    SELECT call_id
    FROM application_form_field_configuration
    WHERE id = 'application.config.project.section.a.3'
);
