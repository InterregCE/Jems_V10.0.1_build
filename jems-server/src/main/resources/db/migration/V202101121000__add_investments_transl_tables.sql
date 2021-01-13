CREATE TABLE project_work_package_investment_transl
(
    investment_id                         INT UNSIGNED NOT NULL,
    language                              VARCHAR(3)   NOT NULL,
    title                                 VARCHAR(50),
    justification_explanation             VARCHAR(2000),
    justification_transactional_relevance VARCHAR(2000),
    justification_benefits                VARCHAR(2000),
    justification_pilot                   VARCHAR(2000),
    risk                                  VARCHAR(2000),
    documentation                         VARCHAR(2000),
    ownership_site_location               VARCHAR(500),
    ownership_retain                      VARCHAR(500),
    ownership_maintenance                 VARCHAR(2000),
    PRIMARY KEY (investment_id, language),
    CONSTRAINT fk_project_work_package_invest_transl_to_project_work_pkg_invest
        FOREIGN KEY (investment_id)
            REFERENCES project_work_package_investment (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_investment
    DROP COLUMN title;
ALTER TABLE project_work_package_investment
    DROP COLUMN justification_explanation;
ALTER TABLE project_work_package_investment
    DROP COLUMN justification_transactional_relevance;
ALTER TABLE project_work_package_investment
    DROP COLUMN justification_benefits;
ALTER TABLE project_work_package_investment
    DROP COLUMN justification_pilot;
ALTER TABLE project_work_package_investment
    DROP COLUMN risk;
ALTER TABLE project_work_package_investment
    DROP COLUMN documentation;
ALTER TABLE project_work_package_investment
    DROP COLUMN ownership_site_location;
ALTER TABLE project_work_package_investment
    DROP COLUMN ownership_retain;
ALTER TABLE project_work_package_investment
    DROP COLUMN ownership_maintenance;