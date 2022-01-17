INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.partner.budget.travel.and.accommodation.comments', id, 'NONE'
FROM project_call
WHERE id not in (SELECT call_id
                 FROM application_form_field_configuration
                 WHERE id = 'application.config.project.partner.budget.travel.and.accommodation.comments');

INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.partner.budget.external.expertise.comments', id, 'NONE'
FROM project_call
WHERE id not in (SELECT call_id
                 FROM application_form_field_configuration
                 WHERE id = 'application.config.project.partner.budget.external.expertise.comments');

INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.partner.budget.equipment.comments', id, 'NONE'
FROM project_call
WHERE id not in (SELECT call_id
                 FROM application_form_field_configuration
                 WHERE id = 'application.config.project.partner.budget.equipment.comments');

INSERT INTO application_form_field_configuration (id, call_id, visibility_status)
SELECT 'application.config.project.partner.budget.infrastructure.and.works.comments', id, 'NONE'
FROM project_call
WHERE id not in (SELECT call_id
                 FROM application_form_field_configuration
                 WHERE id = 'application.config.project.partner.budget.infrastructure.and.works.comments');
