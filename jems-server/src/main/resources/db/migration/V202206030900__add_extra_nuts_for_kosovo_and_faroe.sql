INSERT IGNORE INTO nuts_country(id, title)
SELECT 'FO', 'Faroe Islands' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_region_1(id, nuts_country_id, title)
SELECT 'FO0', 'FO', 'Faroe Islands' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_region_2(id, nuts_region_1_id, title)
SELECT 'FO00', 'FO0', 'Faroe Islands' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_region_3(id, nuts_region_2_id, title)
SELECT 'FO000', 'FO00', 'Faroe Islands' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_country(id, title)
SELECT 'KS', 'Kosovo' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_region_1(id, nuts_country_id, title)
SELECT 'KS0', 'KS', 'Kosovo' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_region_2(id, nuts_region_1_id, title)
SELECT 'KS01', 'KS0', 'Kosovo' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;

INSERT IGNORE INTO nuts_region_3(id, nuts_region_2_id, title)
SELECT 'KS010', 'KS01', 'Kosovo' FROM nuts_metadata WHERE nuts_date IS NOT NULL AND nuts_title IS NOT NULL;
