CREATE TABLE project_audit_correction_financial_description
(
    correction_id                   INT UNSIGNED NOT NULL PRIMARY KEY,
    deduction                       BOOLEAN NOT NULL,
    fund_amount                     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    public_contribution             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    auto_public_contribution        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    private_contribution            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    info_sent_beneficiary_date      DATETIME(3) DEFAULT NULL,
    info_sent_beneficiary_comment   TEXT(2000) DEFAULT NULL,
    correction_type                 VARCHAR(10),
    clerical_technical_mistake      BOOLEAN NOT NULL,
    gold_plating                    BOOLEAN NOT NULL,
    suspected_fraud                 BOOLEAN NOT NULL,
    correction_comment              TEXT(2000) DEFAULT NULL,

    CONSTRAINT fk_correction_financial_desc_to_project_audit_correction FOREIGN KEY (correction_id) REFERENCES project_audit_correction (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
)

