CREATE TABLE project_contracting_partner_state_aid_minimis (
         partner_id                             INT UNSIGNED PRIMARY KEY,
         self_declaration_submission_date       DATETIME(3) DEFAULT NULL,
         base_for_granting                      ENUM ('SUBSIDY_CONTRACT', 'ADDENDUM_SUBSIDY_CONTRACT', 'APPROVAL_OF_MODIFICATION_SUBSIDY_CONTRACT') DEFAULT NULL,
         aid_granted_by_country                 VARCHAR(100) DEFAULT NULL,
         aid_granted_by_country_code            VARCHAR(2) DEFAULT NULL,
         comment                                TEXT(2000) DEFAULT NULL,
         CONSTRAINT fk_project_partner_state_aid_minimis_to_partner
             FOREIGN KEY (partner_id) REFERENCES project_partner (id)
             ON DELETE CASCADE
             ON UPDATE RESTRICT
);

CREATE TABLE project_contracting_partner_state_aid_granted_by_member_state(
           partner_id                   INT UNSIGNED NOT NULL,
           country_code                 VARCHAR(2) NOT NULL,
           country                      VARCHAR(100) NOT NULL,
           amount                       DECIMAL(17, 2) DEFAULT 0.00,
           selected                     BOOLEAN NOT NULL DEFAULT FALSE,
           PRIMARY KEY (partner_id, country_code),
           CONSTRAINT fk_state_aid_minimis_to_project_contracting_state_aid_minimis
               FOREIGN KEY (partner_id) REFERENCES project_contracting_partner_state_aid_minimis (partner_id)
               ON DELETE CASCADE
               ON UPDATE CASCADE
);

CREATE TABLE project_contracting_partner_state_aid_gber(
       partner_id                   INT UNSIGNED NOT NULL PRIMARY KEY,
       aid_intensity                DECIMAL(11, 2) NOT NULL DEFAULT 0.00,
       location_in_assisted_area    ENUM ('A_AREA', 'C_AREA', 'OTHER_AREA', 'NOT_APPLICABLE') DEFAULT NULL,
       comment                      TEXT(2000) DEFAULT NULL,
       CONSTRAINT fk_project_partner_state_aid_gber_to_partner
           FOREIGN KEY (partner_id) REFERENCES project_partner (id)
               ON DELETE CASCADE
               ON UPDATE RESTRICT
);
