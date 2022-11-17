CREATE TABLE project_contracting_management
(
    project_id      INT UNSIGNED NOT NULL,
    management_type ENUM ('ProjectManager', 'FinanceManager', 'CommunicationManager') NOT NULL,
    title           VARCHAR(25)  DEFAULT NULL,
    first_name      VARCHAR(50)  DEFAULT NULL,
    last_name       VARCHAR(50)  DEFAULT NULL,
    email           VARCHAR(255) DEFAULT NULL,
    telephone       VARCHAR(25)  DEFAULT NULL,
    PRIMARY KEY (project_id, management_type),
    CONSTRAINT fk_project_contracting_management_project foreign key (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);


SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectContractingManagementView'),
       (@id, 'ProjectContractingManagementEdit');


