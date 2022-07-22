CREATE TABLE controller_institution
(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(2000) DEFAULT NULL,
    created_at      DATETIME(3) NOT NULL
);

CREATE TABLE controller_institution_nuts
(
    controller_institution_id    INT UNSIGNED NOT NULL,
    nuts_region_3_id VARCHAR(5) NOT NULL,

    CONSTRAINT pk_controller_institution_nuts PRIMARY KEY(controller_institution_id, nuts_region_3_id),
    CONSTRAINT fk_controller_institution_to_controller_institution_id
        FOREIGN KEY (controller_institution_id) REFERENCES controller_institution (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_programme_nuts_to_nuts_region_3_id
        FOREIGN KEY (nuts_region_3_id) REFERENCES nuts_region_3 (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

 CREATE TABLE controller_institution_user
 (
     user_id  INT UNSIGNED NOT NULL,
     controller_institution_id  INT UNSIGNED NOT NULL,
     permission ENUM ('View', 'Edit', 'Manage') NOT NULL DEFAULT 'Edit',

     CONSTRAINT fk_account_to_user_id
        FOREIGN KEY (user_id) REFERENCES account (id)
         ON DELETE CASCADE,
     CONSTRAINT fk_controller_institution_to_controller_institution_user_id
        FOREIGN KEY (controller_institution_id) REFERENCES controller_institution (id)
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (@id, 'InstitutionsUpdate'),
        (@id, 'InstitutionsUnlimited');
