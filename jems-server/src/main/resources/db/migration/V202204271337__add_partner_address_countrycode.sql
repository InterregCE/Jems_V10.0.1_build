SET @@system_versioning_alter_history = 1;

-- PARTNER ADDRESS + fill codes where possible
ALTER TABLE project_partner_address
    ADD COLUMN country_code VARCHAR(2)      AFTER country,
    ADD COLUMN nuts_region2_code VARCHAR(4) AFTER nuts_region2,
    ADD COLUMN nuts_region3_code VARCHAR(5) AFTER nuts_region3;

UPDATE project_partner_address
    SET country_code      = SUBSTRING(REGEXP_SUBSTR(country,      '\\(([[:alpha:]]{2})\\)$'), 2, 2)
    WHERE country      IS NOT NULl;
UPDATE project_partner_address
    SET nuts_region2_code = SUBSTRING(REGEXP_SUBSTR(nuts_region2, '\\(([[:alpha:]]{2}[[:alpha:][:digit:]]{2})\\)$'), 2, 4)
    WHERE nuts_region2 IS NOT NULl;
UPDATE project_partner_address
    SET nuts_region3_code = SUBSTRING(REGEXP_SUBSTR(nuts_region3, '\\(([[:alpha:]]{2}[[:alpha:][:digit:]]{3})\\)$'), 2, 5)
    WHERE nuts_region3 IS NOT NULl;

-- AO ADDRESS + fill codes where possible
ALTER TABLE project_associated_organization_address
    ADD COLUMN country_code VARCHAR(2)      AFTER country,
    ADD COLUMN nuts_region2_code VARCHAR(4) AFTER nuts_region2,
    ADD COLUMN nuts_region3_code VARCHAR(5) AFTER nuts_region3;

UPDATE project_associated_organization_address
    SET country_code      = SUBSTRING(REGEXP_SUBSTR(country,      '\\(([[:alpha:]]{2})\\)$'), 2, 2)
    WHERE country      IS NOT NULl;
UPDATE project_associated_organization_address
    SET nuts_region2_code = SUBSTRING(REGEXP_SUBSTR(nuts_region2, '\\(([[:alpha:]]{2}[[:alpha:][:digit:]]{2})\\)$'), 2, 4)
    WHERE nuts_region2 IS NOT NULl;
UPDATE project_associated_organization_address
    SET nuts_region3_code = SUBSTRING(REGEXP_SUBSTR(nuts_region3, '\\(([[:alpha:]]{2}[[:alpha:][:digit:]]{3})\\)$'), 2, 5)
    WHERE nuts_region3 IS NOT NULl;

-- WP INVESTMENT ADDRESS + fill codes where possible
ALTER TABLE project_work_package_investment
    ADD COLUMN country_code VARCHAR(2)      AFTER country,
    ADD COLUMN nuts_region2_code VARCHAR(4) AFTER nuts_region2,
    ADD COLUMN nuts_region3_code VARCHAR(5) AFTER nuts_region3;

UPDATE project_work_package_investment
    SET country_code      = SUBSTRING(REGEXP_SUBSTR(country,      '\\(([[:alpha:]]{2})\\)$'), 2, 2)
    WHERE country      IS NOT NULl;
UPDATE project_work_package_investment
    SET nuts_region2_code = SUBSTRING(REGEXP_SUBSTR(nuts_region2, '\\(([[:alpha:]]{2}[[:alpha:][:digit:]]{2})\\)$'), 2, 4)
    WHERE nuts_region2 IS NOT NULl;
UPDATE project_work_package_investment
    SET nuts_region3_code = SUBSTRING(REGEXP_SUBSTR(nuts_region3, '\\(([[:alpha:]]{2}[[:alpha:][:digit:]]{3})\\)$'), 2, 5)
    WHERE nuts_region3 IS NOT NULl;
