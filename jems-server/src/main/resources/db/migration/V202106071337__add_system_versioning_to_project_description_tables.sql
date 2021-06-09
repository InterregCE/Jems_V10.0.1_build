-- add system versioning to project application description tables
-- tables: c1, c2, c3, c7, c8

ALTER TABLE project_description_c1_overall_objective_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c1_overall_objective
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c2_relevance_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c2_relevance
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c2_relevance_benefit_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c2_relevance_benefit
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c2_relevance_strategy_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c2_relevance_strategy
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c2_relevance_synergy_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c2_relevance_synergy
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c3_partnership_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c3_partnership
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c7_management_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c7_management
    ADD SYSTEM VERSIONING;

ALTER TABLE project_description_c8_long_term_plans_transl
    ADD SYSTEM VERSIONING;
ALTER TABLE project_description_c8_long_term_plans
    ADD SYSTEM VERSIONING;
