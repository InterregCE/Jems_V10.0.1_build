ALTER TABLE work_package
    RENAME TO project_work_package;

CREATE TABLE programme_fund
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    abbreviation VARCHAR(127)     DEFAULT NULL,
    description  VARCHAR(255)     DEFAULT NULL,
    selected     BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO programme_fund (id, abbreviation)
VALUES (1, 'ERDF'),
       (2, 'ERDF Article 17(3)'),
       (3, 'IPA III CBC'),
       (4, 'Neighbourhood CBC'),
       (5, 'IPA III'),
       (6, 'NDICI'),
       (7, 'OCTP Greenland'),
       (8, 'OCTP'),
       (9, 'Interreg Funds');
