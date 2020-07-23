CREATE TABLE calls
(
    id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    creator_id INTEGER,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(127) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    description VARCHAR(1000),
    CONSTRAINT fk_call_creator_user
        FOREIGN KEY (creator_id) REFERENCES account (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);
