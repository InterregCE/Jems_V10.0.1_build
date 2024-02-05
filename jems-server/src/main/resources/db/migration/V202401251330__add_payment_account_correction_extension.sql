CREATE TABLE payment_account_correction_extension
(
    correction_id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_account_id                 INT UNSIGNED                                                       DEFAULT NULL,
    fund_amount                        DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.0,
    corrected_fund_amount              DECIMAL(17, 2) NOT NULL                                            DEFAULT fund_amount,
    public_contribution                DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.00,
    corrected_public_contribution      DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.00,
    auto_public_contribution           DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.00,
    corrected_auto_public_contribution DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.00,
    private_contribution               DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.00,
    corrected_private_contribution     DECIMAL(17, 2) NOT NULL                                            DEFAULT 0.00,
    comment                            VARCHAR(500)                                                       DEFAULT NULL,
    final_sco_basis                    ENUM ('DoesNotFallUnderArticle94Nor95', 'FallsUnderArticle94Or95') DEFAULT NULL,

    CONSTRAINT fk_account_correction_extension_to_payment_account
        FOREIGN KEY (payment_account_id) REFERENCES payment_account (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,

    CONSTRAINT fk_account_correction_extension_to_audit_control_correction
        FOREIGN KEY (correction_id) REFERENCES audit_control_correction (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO payment_account_correction_extension(correction_id,
                                                 payment_account_id,
                                                 fund_amount,
                                                 public_contribution,
                                                 corrected_public_contribution,
                                                 auto_public_contribution,
                                                 corrected_auto_public_contribution,
                                                 private_contribution,
                                                 corrected_private_contribution)
SELECT correction.id                                                           AS correction_id,
       NULL                                                                    AS payment_account_id,
       accf.fund_amount                                                        AS fund_amount,
       ROUND(accf.public_contribution, 2) * IF(accf.deduction = 1, -1, 1)      AS public_contribution,
       ROUND(accf.public_contribution, 2) * IF(accf.deduction = 1, -1, 1)      AS corrected_public_contribution,
       ROUND(accf.auto_public_contribution, 2) * IF(accf.deduction = 1, -1, 1) AS auto_public_contribution,
       ROUND(accf.auto_public_contribution, 2) * IF(accf.deduction = 1, -1, 1) AS corrected_auto_public_contribution,
       ROUND(accf.private_contribution, 2) * IF(accf.deduction = 1, -1, 1)     AS private_contribution,
       ROUND(accf.private_contribution, 2) * IF(accf.deduction = 1, -1, 1)     AS corrected_private_contribution
FROM audit_control_correction correction
         JOIN audit_control_correction_finance AS accf ON accf.correction_id = correction.id
         JOIN audit_control_correction_measure AS accm ON accm.correction_id = correction.id
WHERE correction.status = 'Closed'
  AND accm.scenario IN ('SCENARIO_3', 'SCENARIO_4');

CREATE TABLE payment_account_priority_axis_overview
(
    id                         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_account_id         INT UNSIGNED        NOT NULL,
    priority_axis_id           INT UNSIGNED                 DEFAULT NULL,
    type                       ENUM ('Correction') NOT NULL,
    total_eligible_expenditure DECIMAL(17, 2)      NOT NULL DEFAULT 0.00,
    total_public_contribution  DECIMAL(17, 2)      NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_account_cumulative_amounts_to_programme_priority
        FOREIGN KEY (priority_axis_id) REFERENCES programme_priority (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_account_cumulative_amounts_to_payment_account
        FOREIGN KEY (payment_account_id) REFERENCES payment_account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    CONSTRAINT uk_overview_payment_and_priority_and_type
        UNIQUE (payment_account_id, priority_axis_id, type)
);

