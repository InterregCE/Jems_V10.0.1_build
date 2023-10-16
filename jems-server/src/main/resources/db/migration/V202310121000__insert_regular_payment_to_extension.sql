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
    SUM(cso.partner_contribution) AS partner_contribution,
    SUM(cso.public_contribution) AS public_contribution,
    SUM(cso.public_contribution) AS corrected_public_contribution,
    SUM(cso.automatic_public_contribution) AS auto_public_contribution,
    SUM(cso.automatic_public_contribution) AS corrected_auto_public_contribution,
    SUM(cso.private_contribution) AS private_contribution,
    SUM(cso.private_contribution) AS corrected_private_contribution
FROM payment p
    INNER JOIN report_project_partner rpp
    ON p.project_report_id = rpp.project_report_id
    INNER JOIN report_project_verification_contribution_source_overview cso
    ON rpp.id = cso.partner_report_id AND p.programme_fund_id = cso.fund_id
WHERE p.type = 'REGULAR' AND cso.fund_id IS NOT NULL
GROUP BY p.id;

ALTER TABLE payment_to_ec_extension
    ADD COLUMN final_sco_basis ENUM('DoesNotFallUnderArticle94Nor95', 'FallsUnderArticle94Or95') DEFAULT NULL;

