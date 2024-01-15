CREATE TABLE payment_application_to_ec_cumulative_amounts
(
    id                           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_application_to_ec_id INT UNSIGNED NOT NULL,
    type                         ENUM('DoesNotFallUnderArticle94Nor95', 'FallsUnderArticle94Or95') NOT NULL,
    priority_axis_id             INT UNSIGNED NOT NULL,
    total_eligible_expenditure   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_union_contribution     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_public_contribution    DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_ec_cumulative_amounts_to_programme_priority
        FOREIGN KEY (priority_axis_id) REFERENCES programme_priority (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_ec_cumulative_amounts_to_payment_applications_to_ec
        FOREIGN KEY (payment_application_to_ec_id) REFERENCES payment_applications_to_ec (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT constraint_unique_id_payment_id_priority_axis UNIQUE (id, payment_application_to_ec_id, priority_axis_id)
)
