DELETE FROM programme_fund_transl WHERE abbreviation = 'OCTP Greenland';
DELETE FROM programme_fund WHERE type = 'OCTP Greenland';
ALTER TABLE programme_fund
    MODIFY COLUMN type ENUM (
        'ERDF',
        'IPA III CBC',
        'Neighbourhood CBC',
        'IPA III',
        'NDICI',
        'OCTP',
        'Interreg Funds',
        'Other')  NOT NULL DEFAULT 'Other';

ALTER TABLE programme_fund_transl
    CHANGE COLUMN `fund_id`
        source_entity_id INT UNSIGNED NOT NULL;
