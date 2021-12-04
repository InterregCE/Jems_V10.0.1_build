INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.investment.expected.delivery.period', id, 'NONE'
FROM project_call
WHERE id not in (SELECT call_id
                 FROM application_form_field_configuration
                 WHERE id = 'application.config.project.investment.expected.delivery.period');

INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.investment.documentation.expected.impacts', id, 'NONE'
FROM project_call
WHERE id not in (SELECT call_id
                 FROM application_form_field_configuration
                 WHERE id = 'application.config.project.investment.documentation.expected.impacts');
