DROP TRIGGER protect_last_system_admin;

DELIMITER $$

CREATE TRIGGER protect_last_system_admin_deletion
    BEFORE DELETE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE count_of_admins INTEGER;

    SELECT COUNT(*) FROM account WHERE account_role_id = 1 INTO count_of_admins;

    IF OLD.account_role_id = 1 AND count_of_admins <= 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'user.lastAdmin.cannot.be.removed';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER protect_last_system_admin_role_update
    BEFORE UPDATE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE count_of_admins INTEGER;

    SELECT COUNT(*) FROM account WHERE account_role_id = 1 INTO count_of_admins;

    IF OLD.account_role_id = 1 AND NEW.account_role_id != 1 AND count_of_admins <= 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'user.lastAdmin.cannot.be.removed';
    END IF;
END$$

DELIMITER ;

