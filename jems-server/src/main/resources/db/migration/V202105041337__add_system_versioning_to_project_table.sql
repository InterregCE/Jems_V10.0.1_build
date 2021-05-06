
ALTER TABLE project_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project
    ADD SYSTEM VERSIONING;

-- the following dependencies are intentionally not historic
-- ALTER TABLE project_eligibility_assessment ADD SYSTEM VERSIONING;
-- ALTER TABLE project_quality_assessment ADD SYSTEM VERSIONING;
-- ALTER TABLE project_status ADD SYSTEM VERSIONING;
