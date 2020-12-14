CREATE TABLE project_work_package_investment
(
    id                                     BINARY(16) PRIMARY KEY,
    work_package_id                        INT UNSIGNED  NOT NULL,
    investment_number                      INT UNSIGNED  NOT NULL,
    title                                  VARCHAR(50),
    justification_explanation              VARCHAR(2000),
    justification_transactional_relevance  VARCHAR(2000),
    justification_benefits                 VARCHAR(2000),
    justification_pilot                    VARCHAR(2000),
    risk                                   VARCHAR(2000),
    documentation                          VARCHAR(2000),
    ownership_site_location                VARCHAR(500),
    ownership_retain                       VARCHAR(500),
    ownership_maintenance                  VARCHAR(2000),
    country                                VARCHAR(100) DEFAULT NULL,
    nuts_region2                           VARCHAR(100) DEFAULT NULL,
    nuts_region3                           VARCHAR(100) DEFAULT NULL,
    street                                 VARCHAR(50)  DEFAULT NULL,
    house_number                           VARCHAR(20)  DEFAULT NULL,
    postal_code                            VARCHAR(20)  DEFAULT NULL,
    city                                   VARCHAR(50)  DEFAULT NULL,
    homepage                               VARCHAR(250) DEFAULT NULL,

    CONSTRAINT fk_project_work_package_investment_work_package FOREIGN KEY (work_package_id) REFERENCES project_work_package (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

