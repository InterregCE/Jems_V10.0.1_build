ALTER TABLE payment
    MODIFY COLUMN amount_approved_per_fund DECIMAL(17,2) NOT NULL DEFAULT 0.00;

ALTER TABLE payment_partner
    MODIFY COLUMN amount_approved_per_partner DECIMAL(17,2) NOT NULL DEFAULT 0.00;
