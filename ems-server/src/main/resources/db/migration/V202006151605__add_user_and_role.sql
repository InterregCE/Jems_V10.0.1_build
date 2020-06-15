CREATE TABLE user_role (
    id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(127) NOT NULL UNIQUE
);

CREATE TABLE user (
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    surname      VARCHAR(255) NOT NULL,
    user_role_id INTEGER      NOT NULL,
    password     VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_user_role
        FOREIGN KEY (user_role_id) REFERENCES user_role (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

INSERT INTO user_role (id, name)
VALUES (1, 'administrator');

INSERT INTO user (email, name, surname, user_role_id, password)
VALUES ('admin', 'Admin', 'Admin', 1, '{bcrypt}$2a$10$CYJy46GgGETq3mKotIVLCuJx3FyTmX4vDNhXy2lSu/lWrOfOVAJ0q');



DELIMITER $$

CREATE OR REPLACE TRIGGER protect_last_system_admin BEFORE DELETE ON user FOR EACH ROW BEGIN
    DECLARE count_of_admins INTEGER;

    SELECT COUNT(*) FROM user WHERE user_role_id = 1 INTO count_of_admins;

    IF OLD.user_role_id = 1 AND count_of_admins <= 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'You cannot remove the last existing admin from the system!';
    END IF;
END$$

DELIMITER ;
