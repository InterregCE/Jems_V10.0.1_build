ALTER TABLE report_project
    ADD COLUMN last_re_submission           DATETIME(3) DEFAULT NULL AFTER first_submission,
    ADD COLUMN last_verification_re_opening DATETIME(3) DEFAULT NULL AFTER verification_end_date,
    MODIFY COLUMN status ENUM (
        'Draft',
        'Submitted',
        'ReOpenSubmittedLast',
        'ReOpenSubmittedLimited',
        'InVerification',
        'VerificationReOpenedLast',
        'VerificationReOpenedLimited',
        'Finalized',
        'ReOpenFinalized'
    ) NOT NULL DEFAULT 'Draft';
