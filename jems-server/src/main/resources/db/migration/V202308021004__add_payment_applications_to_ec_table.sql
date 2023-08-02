CREATE TABLE accounting_years
(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(8) NOT NULL UNIQUE,
    start_date    DATE NOT NULL ,
    end_date      DATE NOT NULL
);

INSERT INTO accounting_years(name, start_date, end_date)
VALUES('Year 1', '2021-01-01', '2022-06-30'),
      ('Year 2', '2022-01-07', '2023-06-30'),
      ('Year 3', '2023-01-07', '2024-06-30'),
      ('Year 4', '2024-01-07', '2025-06-30'),
      ('Year 5', '2025-01-07', '2026-06-30'),
      ('Year 6', '2026-01-07', '2027-06-30'),
      ('Year 7', '2027-01-07', '2028-06-30'),
      ('Year 8', '2028-01-07', '2029-06-30'),
      ('Year 9', '2029-01-07', '2030-06-30');

CREATE TABLE payment_applications_to_ec
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    programme_fund_id INT UNSIGNED NOT NULL,
    accounting_year_id   INT UNSIGNED NOT NULL,
    status            ENUM ('Draft' , 'Finished') NOT NULL DEFAULT 'Draft',
    CONSTRAINT fk_payment_applications_to_ec_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_applications_to_ec_to_accounting_years FOREIGN KEY (accounting_year_id) REFERENCES accounting_years(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (@id, 'PaymentsToEcRetrieve'), (@id, 'PaymentsToEcUpdate');
