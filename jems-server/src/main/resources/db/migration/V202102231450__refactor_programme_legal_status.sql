ALTER TABLE programme_legal_status DROP COLUMN IF EXISTS description;

DELETE FROM programme_legal_status WHERE id IS NOT NULL;

CREATE TABLE programme_legal_status_transl
(
    legal_status_id INT UNSIGNED NOT NULL,
    language        VARCHAR(3)   NOT NULL,
    description     VARCHAR(127) DEFAULT NULL,
    PRIMARY KEY (legal_status_id, language),
    CONSTRAINT fk_programme_legal_status_transl_to_programme_legal_status
        FOREIGN KEY (legal_status_id) REFERENCES programme_legal_status (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO programme_legal_status()
VALUES (),
       ();

SELECT id INTO @id_1 FROM programme_legal_status ORDER BY id ASC LIMIT 1;
SELECT id INTO @id_2 FROM programme_legal_status ORDER BY id DESC LIMIT 1;

INSERT INTO programme_legal_status_transl(legal_status_id, language, description)
VALUES (@id_1, 'EN', 'Public'),
       (@id_2, 'EN', 'Private');
