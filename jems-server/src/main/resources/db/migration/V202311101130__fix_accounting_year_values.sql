UPDATE accounting_years acc
SET acc.start_date = REPLACE(acc.start_date, '01-07', '07-01')
WHERE acc.year > 2021;
