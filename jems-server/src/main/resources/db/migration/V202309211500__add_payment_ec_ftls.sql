CREATE TABLE payment_to_ec_extension
(
    payment_id                         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_application_to_ec_id       INT UNSIGNED            DEFAULT NULL,
    partner_contribution               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    public_contribution                DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    corrected_public_contribution      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    auto_public_contribution           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    corrected_auto_public_contribution DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    private_contribution               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    corrected_private_contribution     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_payment_to_ec_extension foreign key (payment_id) REFERENCES payment (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,

    CONSTRAINT fk_ec_to_payment_applications_to_ec
        FOREIGN KEY (payment_application_to_ec_id) REFERENCES payment_applications_to_ec (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

INSERT INTO payment_to_ec_extension(
    payment_id,
    payment_application_to_ec_id,
    partner_contribution,
    public_contribution,
    corrected_public_contribution,
    auto_public_contribution,
    corrected_auto_public_contribution,
    private_contribution,
    corrected_private_contribution
)
SELECT
    p.id AS payment_id,
    null AS payment_application_to_ec_id,
    p.amount_approved_per_fund AS partner_contribution,
    ROUND(partnerContrib.sumPublicContribution * (p.amount_approved_per_fund / fundSums.sum), 2) AS public_contribution,
    ROUND(partnerContrib.sumPublicContribution * (p.amount_approved_per_fund / fundSums.sum), 2) AS corrected_public_contribution,
    ROUND(partnerContrib.sumAutomaticPublicContribution * (p.amount_approved_per_fund / fundSums.sum), 2) AS auto_public_contribution,
    ROUND(partnerContrib.sumAutomaticPublicContribution * (p.amount_approved_per_fund / fundSums.sum), 2) AS corrected_auto_public_contribution,
    ROUND(partnerContrib.sumPrivateContribution * (p.amount_approved_per_fund / fundSums.sum), 2) AS private_contribution,
    ROUND(partnerContrib.sumPrivateContribution * (p.amount_approved_per_fund / fundSums.sum), 2) AS corrected_private_contribution
FROM payment p
         LEFT JOIN (
    SELECT
        p2.project_lump_sum_id AS projectId,
        p2.order_nr AS orderNr,
        SUM(p2.amount_approved_per_fund) AS sum
    FROM payment p2
    GROUP BY p2.project_lump_sum_id, p2.order_nr
) fundSums ON p.project_id = fundSums.projectId AND p.order_nr = fundSums.orderNr
         LEFT JOIN (
    SELECT
        pcm.project_id AS projectId,
        pcm.order_nr AS orderNr,
        SUM(pcm.partner_contribution) AS sumPartnerContribution,
        SUM(pcm.public_contribution) AS sumPublicContribution,
        SUM(pcm.automatic_public_contribution) AS sumAutomaticPublicContribution,
        SUM(pcm.private_contribution) AS sumPrivateContribution
    FROM payment_contribution_meta pcm
    GROUP BY pcm.project_id, pcm.order_nr
) partnerContrib ON p.project_id = partnerContrib.projectId AND p.order_nr = partnerContrib.orderNr
WHERE p.type = 'FTLS'
ORDER BY p.project_id, p.order_nr;

UPDATE project_contracting_monitoring SET typology_prov_94 = 'No' WHERE typology_prov_94 IS NULL;
UPDATE project_contracting_monitoring SET typology_prov_95 = 'No' WHERE typology_prov_95 IS NULL;
UPDATE project_contracting_monitoring SET typology_strategic = 'No' WHERE typology_strategic IS NULL;
UPDATE project_contracting_monitoring SET typology_partnership = 'No' WHERE typology_partnership IS NULL;

ALTER TABLE project_contracting_monitoring
    MODIFY COLUMN typology_prov_94      ENUM ('Yes', 'No', 'Partly') NOT NULL DEFAULT 'No',
    MODIFY COLUMN typology_prov_95      ENUM ('Yes', 'No', 'Partly') NOT NULL DEFAULT 'No',
    MODIFY COLUMN typology_strategic    ENUM ('Yes', 'No') NOT NULL DEFAULT 'No' ,
    MODIFY COLUMN typology_partnership  ENUM ('Yes', 'No') NOT NULL DEFAULT 'No';


