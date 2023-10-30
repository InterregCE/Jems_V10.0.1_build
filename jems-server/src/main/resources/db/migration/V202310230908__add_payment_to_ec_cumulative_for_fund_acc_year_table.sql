RENAME TABLE payment_application_to_ec_cumulative_amounts TO payment_application_to_ec_priority_axis_overview;

CREATE TABLE payment_application_to_ec_priority_axis_cumulative_overview
(
    payment_application_to_ec_id    INT UNSIGNED NOT NULL,
    priority_axis_id                INT UNSIGNED NOT NULL,
    total_eligible_expenditure      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_union_contribution        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_public_contribution       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    PRIMARY KEY (payment_application_to_ec_id, priority_axis_id),

    CONSTRAINT fk_ec_cumulative_amounts_fund_year_to_programme_priority
        FOREIGN KEY (priority_axis_id) REFERENCES programme_priority (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_ec_cumulative_amounts_fund_year_to_payment_applications_to_ec
        FOREIGN KEY (payment_application_to_ec_id) REFERENCES payment_applications_to_ec (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT

)
