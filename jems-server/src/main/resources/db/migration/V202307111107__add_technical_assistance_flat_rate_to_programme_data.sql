ALTER TABLE programme_data
    ADD COLUMN technical_assistance_flat_rate DECIMAL(5, 2) NOT NULL DEFAULT 0.00 AFTER programme_amending_decision_date;
