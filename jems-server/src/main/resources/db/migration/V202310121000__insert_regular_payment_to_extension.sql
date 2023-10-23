ALTER TABLE payment_application_to_ec_cumulative_amounts
    MODIFY COLUMN priority_axis_id INT UNSIGNED DEFAULT NULL;

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
FROM report_project_verification_contribution_source_overview cso
         LEFT JOIN report_project_partner rpp ON cso.partner_report_id = rpp.id
         LEFT JOIN report_project rp ON rpp.project_report_id = rp.id
         LEFT JOIN payment p ON rp.id = p.project_report_id AND cso.fund_id = p.programme_fund_id
WHERE cso.fund_id IS NOT NULL
GROUP BY rp.id, cso.fund_id;

ALTER TABLE payment_to_ec_extension
    ADD COLUMN final_sco_basis ENUM('DoesNotFallUnderArticle94Nor95', 'FallsUnderArticle94Or95') DEFAULT NULL
        AFTER payment_application_to_ec_id;
