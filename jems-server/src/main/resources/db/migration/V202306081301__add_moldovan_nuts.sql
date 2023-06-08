INSERT IGNORE INTO nuts_region_1(id, nuts_country_id, title)
VALUES ('MD1', 'MD', 'Moldova');

INSERT IGNORE INTO nuts_region_2(id, nuts_region_1_id, title)
VALUES ('MD10', 'MD1', 'Chișinău municipality'),
       ('MD11', 'MD1', 'Balți municipality'),
       ('MD12', 'MD1', 'Bender municipality'),
       ('MD13', 'MD1', 'UTA Gagauzia*'),
       ('MD14', 'MD1', 'UATSN*');

INSERT IGNORE INTO nuts_region_3(id, nuts_region_2_id, title)
VALUES ('MD100', 'MD10', 'Chișinău municipality'),
       ('MD110', 'MD11', 'Balți municipality'),
       ('MD120', 'MD12', 'Bender municipality'),
       ('MD130', 'MD13', 'UTA Gagauzia*'),
       ('MD140', 'MD14', 'UATSN*');
