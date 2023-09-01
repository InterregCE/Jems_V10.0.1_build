ALTER TABLE project_call
    ADD COLUMN is_direct_contributions_allowed BOOLEAN NOT NULL DEFAULT TRUE AFTER is_additional_fund_allowed;
