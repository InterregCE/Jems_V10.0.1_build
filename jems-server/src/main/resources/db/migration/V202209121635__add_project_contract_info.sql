CREATE TABLE project_contract_info (
    project_id INT UNSIGNED,
    website VARCHAR(30) DEFAULT NULL,
    partnership_agreement_date DATE DEFAULT NULL,
    PRIMARY KEY (project_id),
    CONSTRAINT fk_contract_info_to_project FOREIGN KEY (project_id) references project (id)
       ON DELETE RESTRICT
       ON UPDATE RESTRICT
);


SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectContractsView'),
       (@id, 'ProjectContractsEdit');
