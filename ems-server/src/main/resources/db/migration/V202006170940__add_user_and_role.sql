CREATE TABLE account_role
(
    id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(127) NOT NULL UNIQUE
);

CREATE TABLE account
(
    id              INTEGER AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    surname         VARCHAR(255) NOT NULL,
    account_role_id INTEGER      NOT NULL,
    password        VARCHAR(255) NOT NULL,
    CONSTRAINT fk_account_account_role
        FOREIGN KEY (account_role_id) REFERENCES account_role (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

INSERT INTO account_role (id, name)
VALUES (1, 'ADMIN');

INSERT INTO account (email, name, surname, account_role_id, password)
VALUES ('admin', 'Admin', 'Admin', 1, '{bcrypt}$2a$10$YbArQmvqQJVXXGehyHrJK.HlZv.FH29ropwqf/WaIRMKjOWVmMrqm');


DELIMITER $$

CREATE OR REPLACE TRIGGER protect_last_system_admin
    BEFORE DELETE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE count_of_admins INTEGER;

    SELECT COUNT(*) FROM account WHERE account_role_id = 1 INTO count_of_admins;

    IF OLD.account_role_id = 1 AND count_of_admins <= 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'You cannot remove the last existing admin from the system!';
    END IF;
END$$

DELIMITER ;
