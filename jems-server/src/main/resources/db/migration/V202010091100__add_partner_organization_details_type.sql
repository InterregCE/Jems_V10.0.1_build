DROP TABLE project_partner_organization_details;

CREATE TABLE project_partner_organization_details
(
    organization_id INT UNSIGNED                        NOT NULL,
    type            ENUM ('Organization', 'Department') NOT NULL,
    country         VARCHAR(100) DEFAULT NULL,
    nuts_region2    VARCHAR(100) DEFAULT NULL,
    nuts_region3    VARCHAR(100) DEFAULT NULL,
    street          VARCHAR(50)  DEFAULT NULL,
    house_number    VARCHAR(20)  DEFAULT NULL,
    postal_code     VARCHAR(20)  DEFAULT NULL,
    city            VARCHAR(50)  DEFAULT NULL,
    homepage        VARCHAR(250) DEFAULT NULL,
    PRIMARY KEY (organization_id, type),
    CONSTRAINT fk_partner_organization_details_to_partner_organization FOREIGN KEY (organization_id) REFERENCES project_partner_organization (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
