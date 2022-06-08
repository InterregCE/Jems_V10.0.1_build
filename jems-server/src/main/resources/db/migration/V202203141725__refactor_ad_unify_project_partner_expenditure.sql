ALTER TABLE partner_report_expenditure_cost
    RENAME TO report_project_partner_expenditure;
ALTER TABLE partner_report_expenditure_cost_transl
    RENAME TO report_project_partner_expenditure_transl;

ALTER TABLE report_project_partner_expenditure
    CHANGE invoice_date invoice_date DATE DEFAULT NULL,
    CHANGE date_of_payment date_of_payment DATE DEFAULT NULL;
