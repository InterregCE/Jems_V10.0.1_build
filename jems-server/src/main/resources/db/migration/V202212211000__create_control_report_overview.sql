CREATE TABLE report_project_partner_control_overview
(
    partner_report_id     		   INT UNSIGNED PRIMARY KEY,
    start_date 			  		   DATE         NOT NULL,
    requests_for_clarifications      	TEXT(1000),
    receipt_of_satisfactory_answers   	TEXT(1000),
    end_date 			  		   		DATE,
    finding_description      	   		TEXT(5000),
    follow_up_measures_from_last_report TEXT(5000),
    conclusion      			   		TEXT(5000),
    follow_up_measures_for_next_report  TEXT(5000),
    last_certified_report_id_when_creation INT,
    CONSTRAINT fk_report_partner_control_overview_to_report_partner
        FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
