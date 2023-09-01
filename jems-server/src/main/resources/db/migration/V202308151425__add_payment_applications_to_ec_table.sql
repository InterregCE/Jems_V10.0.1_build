CREATE TABLE accounting_years
(
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    year       SMALLINT NOT NULL UNIQUE,
    start_date DATE     NOT NULL,
    end_date   DATE     NOT NULL
);

INSERT INTO accounting_years(year, start_date, end_date)
VALUES (2021, '2021-01-01', '2022-06-30'),
       (2022, '2022-01-07', '2023-06-30'),
       (2023, '2023-01-07', '2024-06-30'),
       (2024, '2024-01-07', '2025-06-30'),
       (2025, '2025-01-07', '2026-06-30'),
       (2026, '2026-01-07', '2027-06-30'),
       (2027, '2027-01-07', '2028-06-30'),
       (2028, '2028-01-07', '2029-06-30'),
       (2029, '2029-01-07', '2030-06-30');

CREATE TABLE payment_applications_to_ec
(
    id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    programme_fund_id  INT UNSIGNED                NOT NULL,
    accounting_year_id INT UNSIGNED                NOT NULL,
    status             ENUM ('Draft' , 'Finished') NOT NULL DEFAULT 'Draft',
    CONSTRAINT fk_payment_applications_to_ec_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_applications_to_ec_to_accounting_years FOREIGN KEY (accounting_year_id) REFERENCES accounting_years (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (@id, 'PaymentsToEcRetrieve'), (@id, 'PaymentsToEcUpdate');
