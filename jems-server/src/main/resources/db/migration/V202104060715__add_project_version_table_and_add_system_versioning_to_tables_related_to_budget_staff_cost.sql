CREATE TABLE project_version
(
    version    INT UNSIGNED          NOT NULL,
    project_id INT UNSIGNED          NOT NULL,
    account_id INT UNSIGNED          NOT NULL,
    created_at DATETIME(6)           NOT NULL,
    row_end    DATETIME(6) INVISIBLE NOT NULL DEFAULT localtimestamp,
    status     VARCHAR(127)          NOT NULL,
    PRIMARY KEY (project_id, version)
);

ALTER TABLE project_partner_budget_staff_cost_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_staff_cost_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_staff_cost
    ADD SYSTEM VERSIONING;


