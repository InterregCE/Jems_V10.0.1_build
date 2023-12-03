UPDATE audit_control_correction_finance
SET fund_amount              = fund_amount * -1,
    public_contribution      = public_contribution * -1,
    auto_public_contribution = auto_public_contribution * -1,
    private_contribution     = private_contribution * -1
WHERE deduction IS TRUE;
