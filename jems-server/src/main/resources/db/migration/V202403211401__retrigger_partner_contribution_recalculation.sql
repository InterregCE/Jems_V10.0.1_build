UPDATE payment_to_ec_extension pteeToUpdate
INNER JOIN (
    SELECT
        p.id AS paymentId,
        p.project_id,
        p.order_nr,
        p.programme_fund_id AS fundId,
        p.amount_approved_per_fund AS amountPerFund,
        p.amount_approved_per_fund + ptee.partner_contribution AS wrongTotal,
        ptee.partner_contribution AS wrongPartnerContrib,
        SUM(ROUND((toSplit.toSplit / toSplit.splittingBase) * pp.amount_approved_per_partner, 2)) AS newPartnerContrib
    FROM payment_partner pp
        LEFT JOIN payment p ON pp.payment_id = p.id
        LEFT JOIN payment_to_ec_extension ptee ON p.id = ptee.payment_id
        LEFT JOIN (
            SELECT
                pls.project_id AS projectId,
                pls.order_nr AS orderNr,
                pp.partner_id AS partnerId,
                SUM(pp.amount_approved_per_partner) AS splittingBase,
                ppls.amount - SUM(pp.amount_approved_per_partner) AS toSplit
            FROM project_partner_lump_sum ppls
                LEFT JOIN project_lump_sum pls ON ppls.project_id = pls.project_id and ppls.order_nr = pls.order_nr
                LEFT JOIN payment p ON pls.project_id = p.project_lump_sum_id and pls.order_nr = p.order_nr
                LEFT JOIN payment_partner pp ON p.id = pp.payment_id AND pp.partner_id = ppls.project_partner_id
            WHERE
                pp.partner_id IS NOT NULL
            GROUP BY pls.project_id, pls.order_nr, pp.partner_id
        ) toSplit ON toSplit.projectId = p.project_id AND toSplit.orderNr = p.order_nr AND toSplit.partnerId = pp.partner_id
    WHERE p.type = 'FTLS'
    GROUP BY p.id
) toUpdate ON pteeToUpdate.payment_id = toUpdate.paymentId
SET pteeToUpdate.partner_contribution = toUpdate.newPartnerContrib;
