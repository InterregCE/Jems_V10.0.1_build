CREATE TABLE programme_data
(
    id                                 INTEGER AUTO_INCREMENT PRIMARY KEY,
    cci                                VARCHAR(15),
    title                              VARCHAR(255),
    version                            VARCHAR(255),
    first_year                         INTEGER,
    last_year                          INTEGER,
    eligible_from                      DATE,
    eligible_until                     DATE,
    commission_decision_number         VARCHAR(255),
    commission_decision_date           DATE,
    programme_amending_decision_number VARCHAR(255),
    programme_amending_decision_date   DATE
);

INSERT INTO programme_data (id)
VALUES (1);
