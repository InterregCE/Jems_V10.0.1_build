CREATE TABLE programme_indicator_output
(
    id                           INTEGER AUTO_INCREMENT PRIMARY KEY,
    identifier                   VARCHAR(5)   NOT NULL,
    code                         VARCHAR(6)    DEFAULT NULL,
    name                         VARCHAR(255) NOT NULL,
    programme_priority_policy_id VARCHAR(127)  DEFAULT NULL,
    measurement_unit             VARCHAR(255)  DEFAULT NULL,
    milestone                    DECIMAL(9, 2) DEFAULT NULL,
    final_target                 DECIMAL(9, 2) DEFAULT NULL,
    CONSTRAINT fk_programme_indicator_output_to_programme_priority_policy
        FOREIGN KEY (programme_priority_policy_id) REFERENCES programme_priority_policy (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE programme_indicator_result
(
    id                           INTEGER AUTO_INCREMENT PRIMARY KEY,
    identifier                   VARCHAR(5)   NOT NULL,
    code                         VARCHAR(6)                      DEFAULT NULL,
    name                         VARCHAR(255) NOT NULL,
    programme_priority_policy_id VARCHAR(127)                    DEFAULT NULL,
    measurement_unit             VARCHAR(255)                    DEFAULT NULL,
    baseline                     DECIMAL(9, 2) UNSIGNED ZEROFILL DEFAULT NULL,
    reference_year               VARCHAR(10)                     DEFAULT NULL,
    final_target                 DECIMAL(9, 2)                   DEFAULT NULL,
    source_of_data               TEXT                            DEFAULT NULL,
    comment                      TEXT                            DEFAULT NULL,
    CONSTRAINT fk_programme_indicator_result_to_programme_priority_policy
        FOREIGN KEY (programme_priority_policy_id) REFERENCES programme_priority_policy (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);
