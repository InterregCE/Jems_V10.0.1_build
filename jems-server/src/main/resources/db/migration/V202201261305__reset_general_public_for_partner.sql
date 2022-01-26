SET @@system_versioning_alter_history = 1;

UPDATE project_partner
SET partner_type = NULL
WHERE partner_type = 'GeneralPublic';
