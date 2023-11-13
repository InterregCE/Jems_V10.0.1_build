CREATE TABLE payment_to_ec_correction_extension
(
    correction_id                      INT UNSIGNED  AUTO_INCREMENT PRIMARY KEY,
    payment_application_to_ec_id       INT UNSIGNED  DEFAULT NULL,
    fund_amount                        DECIMAL(17, 2) NOT NULL DEFAULT 0.0,
    public_contribution                DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    corrected_public_contribution      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    auto_public_contribution           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    corrected_auto_public_contribution DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    private_contribution               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    corrected_private_contribution     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    comment                            VARCHAR(500)   DEFAULT NULL,
    final_sco_basis ENUM('DoesNotFallUnderArticle94Nor95', 'FallsUnderArticle94Or95') DEFAULT NULL,

    CONSTRAINT fk_ec_correction_extension_to_payment_applications_to_ec
        FOREIGN KEY (payment_application_to_ec_id) REFERENCES payment_applications_to_ec (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,

    CONSTRAINT fk_ec_correction_extension_to_audit_control_correction
        FOREIGN KEY (correction_id) REFERENCES audit_control_correction (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO payment_to_ec_correction_extension(
    correction_id,
    payment_application_to_ec_id,
    fund_amount,
    public_contribution,
    corrected_public_contribution,
    auto_public_contribution,
    corrected_auto_public_contribution,
    private_contribution,
    corrected_private_contribution
)
SELECT
    correction.id AS correction_id,
    null AS payment_application_to_ec_id,
    accf.fund_amount AS fund_amount,
    ROUND(accf.public_contribution, 2) AS public_contribution,
    ROUND(accf.public_contribution, 2) AS corrected_public_contribution,
    ROUND(accf.auto_public_contribution, 2) AS auto_public_contribution,
    ROUND(accf.auto_public_contribution, 2) AS corrected_auto_public_contribution,
    ROUND(accf.private_contribution, 2) AS private_contribution,
    ROUND(accf.private_contribution, 2) AS corrected_private_contribution
FROM audit_control_correction correction
JOIN audit_control_correction_finance AS accf ON accf.correction_id = correction.id
JOIN audit_control_correction_measure AS accm ON accm.correction_id = correction.id
WHERE correction.status = 'Closed' AND accm.scenario IN ('NA', 'SCENARIO_2', 'SCENARIO_5') AND accf.deduction = 0;

INSERT INTO payment_to_ec_correction_extension(
    correction_id,
    payment_application_to_ec_id,
    fund_amount,
    public_contribution,
    corrected_public_contribution,
    auto_public_contribution,
    corrected_auto_public_contribution,
    private_contribution,
    corrected_private_contribution
)
SELECT
    correction.id AS correction_id,
    null AS payment_application_to_ec_id,
    -accf.fund_amount AS fund_amount,
    ROUND(-accf.public_contribution, 2) AS public_contribution,
    ROUND(-accf.public_contribution, 2) AS corrected_public_contribution,
    ROUND(-accf.auto_public_contribution, 2) AS auto_public_contribution,
    ROUND(-accf.auto_public_contribution, 2) AS corrected_auto_public_contribution,
    ROUND(-accf.private_contribution, 2) AS private_contribution,
    ROUND(-accf.private_contribution, 2) AS corrected_private_contribution
FROM audit_control_correction correction
         JOIN audit_control_correction_finance AS accf ON accf.correction_id = correction.id
         JOIN audit_control_correction_measure AS accm ON accm.correction_id = correction.id
WHERE correction.status = 'Closed' AND accm.scenario IN ('NA', 'SCENARIO_2', 'SCENARIO_5') AND accf.deduction = 1;

ALTER TABLE payment_application_to_ec_priority_axis_overview
CHANGE COLUMN type type ENUM('DoesNotFallUnderArticle94Nor95', 'FallsUnderArticle94Or95', 'Correction');

ALTER TABLE payment_application_to_ec_priority_axis_overview
    DROP CONSTRAINT fk_ec_cumulative_amounts_to_payment_applications_to_ec,
    DROP CONSTRAINT fk_ec_cumulative_amounts_to_programme_priority,
    DROP INDEX payment_application_to_ec_id;

ALTER TABLE payment_application_to_ec_priority_axis_overview
    ADD CONSTRAINT fk_ec_cumulative_amounts_to_programme_priority
        FOREIGN KEY (priority_axis_id) REFERENCES programme_priority (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_ec_cumulative_amounts_to_payment_applications_to_ec
         FOREIGN KEY (payment_application_to_ec_id) REFERENCES payment_applications_to_ec (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    ADD CONSTRAINT unq_overview_payment_and_priority_and_type
        UNIQUE (payment_application_to_ec_id, priority_axis_id, type);
