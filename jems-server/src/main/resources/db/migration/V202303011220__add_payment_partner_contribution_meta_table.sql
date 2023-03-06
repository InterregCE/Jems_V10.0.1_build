CREATE TABLE payment_contribution_meta (
    id                            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id                    INT UNSIGNED     NOT NULL,
    partner_id                    INT UNSIGNED     NOT NULL,

    programme_lump_sum_id         INT UNSIGNED     NOT NULL,
    order_nr                      TINYINT UNSIGNED NOT NULL,

    partner_contribution          DECIMAL(17, 2)   NOT NULL DEFAULT 0.00,
    public_contribution           DECIMAL(17, 2)   NOT NULL DEFAULT 0.00,
    automatic_public_contribution DECIMAL(17, 2)   NOT NULL DEFAULT 0.00,
    private_contribution          DECIMAL(17, 2)   NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_payment_partner_contrib_to_lump_sum FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

# for all FastTrack lumpSums, that were already mark ready, we calculate partner contribution and store it
INSERT INTO payment_contribution_meta(project_id, partner_id, programme_lump_sum_id, order_nr, partner_contribution)
SELECT
    ppls.project_id,
    ppls.project_partner_id,
    payment.programmeLumpSumId,
    payment.orderNr,
    ppls.amount - payment.amount AS partnerContrib
FROM project_lump_sum projectLS
    INNER JOIN project_version AS v ON v.project_id = projectLS.project_id AND v.version = projectLS.last_approved_version_before_ready_for_payment
    LEFT JOIN project_partner_lump_sum FOR SYSTEM_TIME AS OF TIMESTAMP IFNULL(v.row_end, NOW()) AS ppls
        ON projectLS.project_id = ppls.project_id and projectLS.order_nr = ppls.order_nr
    INNER JOIN (
        SELECT
            p.programme_lump_sum_id AS programmeLumpSumId,
            p.order_nr AS orderNr,
            pp.partner_id AS partnerId,
            SUM(pp.amount_approved_per_partner) AS amount
        FROM payment p
            LEFT JOIN payment_partner pp ON p.id = pp.payment_id
        GROUP BY p.programme_lump_sum_id, p.order_nr, pp.partner_id
    ) payment
        ON payment.programmeLumpSumId = projectLS.programme_lump_sum_id
            AND payment.orderNr = projectLS.order_nr
            AND payment.partnerId = ppls.project_partner_id
WHERE projectLS.is_ready_for_payment = 1;
