DELETE
FROM project_partner_co_financing
WHERE partner_id IS NOT NULL;

DELETE
FROM project_call
WHERE id IN (SELECT DISTINCT call_id FROM project_call_fund);

DELETE
FROM programme_fund
WHERE id IS NOT NULL;

ALTER TABLE programme_fund
    DROP COLUMN abbreviation;
ALTER TABLE programme_fund
    DROP COLUMN description;

CREATE TABLE programme_fund_transl
(
    fund_id      INT UNSIGNED NOT NULL,
    language     VARCHAR(3)   NOT NULL,
    abbreviation VARCHAR(127) DEFAULT NULL,
    description  VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (fund_id, language),
    CONSTRAINT fk_programme_fund_transl_to_programme_fund
        FOREIGN KEY (fund_id) REFERENCES programme_fund (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO programme_fund(selected)
VALUES (true);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'ERDF');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'ERDF Article 17(3)');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'IPA III CBC');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'Neighbourhood CBC');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'IPA III');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'NDICI');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'OCTP Greenland');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'OCTP');

INSERT INTO programme_fund(selected)
VALUES (false);

SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation) VALUES (@id, 'EN', 'Interreg Funds');
