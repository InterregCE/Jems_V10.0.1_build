CREATE TABLE programme_language
(
    code        VARCHAR(3)  NOT NULL PRIMARY KEY,
    ui          BOOLEAN     NOT NULL DEFAULT FALSE,
    fallback    BOOLEAN     NOT NULL DEFAULT FALSE,
    input       BOOLEAN     NOT NULL DEFAULT FALSE
);

INSERT INTO programme_language (code, ui, fallback, input)
VALUES ('BE', false, false, false),
       ('BG', false, false, false),
       ('CS', false, false, false),
       ('DA', false, false, false),
       ('DE', false, false, false),
       ('EL', false, false, false),
       ('EN', true,  true,  true ),
       ('ES', false, false, false),
       ('ET', false, false, false),
       ('FI', false, false, false),
       ('FR', false, false, false),
       ('GA', false, false, false),
       ('HR', false, false, false),
       ('HU', false, false, false),
       ('IT', false, false, false),
       ('JA', false, false, false),
       ('LB', false, false, false),
       ('LT', false, false, false),
       ('LV', false, false, false),
       ('MT', false, false, false),
       ('MK', false, false, false),
       ('NL', false, false, false),
       ('NO', false, false, false),
       ('PL', false, false, false),
       ('PT', false, false, false),
       ('RO', false, false, false),
       ('RU', false, false, false),
       ('SK', false, false, false),
       ('SL', false, false, false),
       ('SQ', false, false, false),
       ('SR', false, false, false),
       ('SV', false, false, false),
       ('TR', false, false, false),
       ('UK', false, false, false);

ALTER TABLE programme_data  DROP COLUMN languages_system;
