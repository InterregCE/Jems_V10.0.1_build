ALTER TABLE payment_application_to_ec_priority_axis_overview
    DROP CONSTRAINT constraint_unique_id_payment_id_priority_axis,
    ADD CONSTRAINT unq_overview_payment_and_priority
        UNIQUE (payment_application_to_ec_id, priority_axis_id);

DROP TABLE payment_application_to_ec_priority_axis_cumulative_overview;

CREATE TABLE payment_application_to_ec_priority_axis_cumulative_overview
(
    id                           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_application_to_ec_id INT UNSIGNED NOT NULL,
    priority_axis_id             INT UNSIGNED NULL,
    total_eligible_expenditure   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_union_contribution     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_public_contribution    DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_ec_cumulative_amounts_fund_year_to_programme_priority
        FOREIGN KEY (priority_axis_id) REFERENCES programme_priority (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_ec_cumulative_amounts_fund_year_to_payment_application_to_ec
        FOREIGN KEY (payment_application_to_ec_id) REFERENCES payment_applications_to_ec (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT unq_cumulative_overview_payment_and_priority
        UNIQUE (payment_application_to_ec_id, priority_axis_id)
)
