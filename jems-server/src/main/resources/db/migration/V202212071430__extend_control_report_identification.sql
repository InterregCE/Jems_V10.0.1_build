CREATE TABLE report_project_partner_designated_controller (
    report_id        INT UNSIGNED NOT NULL PRIMARY KEY,
    institution_id   INT UNSIGNED NOT NULL,
    institution_name VARCHAR(250) DEFAULT NULL,
    control_user_id  INT UNSIGNED DEFAULT NULL,
    review_user_id   INT UNSIGNED DEFAULT NULL,
    job_title        TEXT(50) DEFAULT NULL,
    division_unit    TEXT(100) DEFAULT NULL,
    address          TEXT(100) DEFAULT NULL,
    country_code     TEXT(2) DEFAULT '',
    country          TEXT(100) DEFAULT '',
    telephone        VARCHAR(100) DEFAULT NULL,
    CONSTRAINT fk_report_partner_designated_controller_to_report_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
          ON DELETE CASCADE
          ON UPDATE RESTRICT,
    CONSTRAINT fk_report_partner_designated_controller_to_institution
        FOREIGN KEY (institution_id) REFERENCES controller_institution (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_partner_designated_controller_to_control_user
        FOREIGN KEY (control_user_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_partner_designated_controller_to_review_user
        FOREIGN KEY (review_user_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_verification (
    report_id                           INT UNSIGNED NOT NULL PRIMARY KEY,
    risk_based_verification_applied     BOOLEAN NOT NULL DEFAULT FALSE,
    risk_based_verification_description TEXT(5000) DEFAULT NULL,
    CONSTRAINT fk_report_partner_verification_to_report_partner
     FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
         ON DELETE CASCADE
         ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_on_the_spot_verification (
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    report_verification_id INT UNSIGNED NOT NULL,
    verification_from      DATETIME(3) DEFAULT NULL,
    verification_to        DATETIME(3) DEFAULT NULL,
    verification_focus     TEXT(3000) DEFAULT NULL,
    CONSTRAINT fk_on_the_spot_verification_to_verification
        FOREIGN KEY (report_verification_id) REFERENCES report_project_partner_verification (report_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_verification_on_the_spot_location
(
    id                                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    report_on_the_spot_verification_id INT UNSIGNED NOT NULL,
    location               ENUM (
        'PremisesOfProjectPartner',
        'ProjectEvent',
        'PlaceOfPhysicalProjectOutput',
        'Virtual' )         NOT NULL,
    CONSTRAINT fk_verification_on_spot_location_to_verification_on_spot
        FOREIGN KEY (report_on_the_spot_verification_id) REFERENCES report_project_partner_on_the_spot_verification (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_verification_general_methodology
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    report_verification_id INT UNSIGNED NOT NULL,
    methodology            ENUM (
        'AdministrativeVerification',
        'OnTheSpotVerification' )         NOT NULL,
    CONSTRAINT fk_verification_general_methodology_to_verification
        FOREIGN KEY (report_verification_id) REFERENCES report_project_partner_verification (report_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

DELETE FROM report_project_partner WHERE status = 'InControl';
