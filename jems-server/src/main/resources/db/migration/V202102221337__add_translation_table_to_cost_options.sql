-- add programme_lump_sum_transl

ALTER TABLE programme_lump_sum DROP COLUMN name;
ALTER TABLE programme_lump_sum DROP COLUMN description;

CREATE TABLE programme_lump_sum_transl
(
    programme_lump_sum_id   INT UNSIGNED    NOT NULL,
    language                VARCHAR(3)      NOT NULL,
    name                    TEXT(50)        DEFAULT NULL,
    description             TEXT(500)       DEFAULT NULL,
    PRIMARY KEY (programme_lump_sum_id, language),
    CONSTRAINT fk_programme_lump_sum_transl_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id)
            REFERENCES programme_lump_sum (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

-- add programme_unit_cost_transl

ALTER TABLE programme_unit_cost DROP COLUMN name;
ALTER TABLE programme_unit_cost DROP COLUMN description;
ALTER TABLE programme_unit_cost DROP COLUMN type;

CREATE TABLE programme_unit_cost_transl
(
    programme_unit_cost_id  INT UNSIGNED    NOT NULL,
    language                VARCHAR(3)      NOT NULL,
    name                    TEXT(50)        DEFAULT NULL,
    description             TEXT(500)       DEFAULT NULL,
    type                    TEXT(25)        DEFAULT NULL,
    PRIMARY KEY (programme_unit_cost_id, language),
    CONSTRAINT fk_programme_unit_cost_transl_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id)
            REFERENCES programme_unit_cost (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
