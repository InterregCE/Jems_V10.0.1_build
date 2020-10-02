CREATE TABLE programme_legal_status
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    description       VARCHAR(127)     DEFAULT NULL
);

INSERT INTO programme_legal_status (id, description)
VALUES (1, 'Public'),
       (2, 'Private');
