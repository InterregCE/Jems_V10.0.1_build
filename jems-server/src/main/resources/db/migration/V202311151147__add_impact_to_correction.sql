ALTER TABLE audit_control_correction
    ADD COLUMN impact         ENUM ('NA', 'RepaymentByProject', 'AdjustmentInNextPayment', 'BudgetReduction', 'RepaymentByNA') NOT NULL DEFAULT 'NA',
    ADD COLUMN impact_comment VARCHAR(2000)                                                                                    NOT NULL DEFAULT '';
