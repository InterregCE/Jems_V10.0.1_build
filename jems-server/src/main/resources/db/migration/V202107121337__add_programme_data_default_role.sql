-- add foreign key to indicate default user role
ALTER TABLE programme_data
    ADD COLUMN default_user_role_id INT UNSIGNED DEFAULT NULL
        AFTER programme_amending_decision_date,
    ADD CONSTRAINT fk_to_account_role
        FOREIGN KEY (default_user_role_id) REFERENCES account_role (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

-- set applicant role to default role
SELECT id INTO @id FROM account_role WHERE `name` = 'applicant user' ORDER BY id DESC LIMIT 1;
UPDATE programme_data SET default_user_role_id = @id WHERE id = 1;
