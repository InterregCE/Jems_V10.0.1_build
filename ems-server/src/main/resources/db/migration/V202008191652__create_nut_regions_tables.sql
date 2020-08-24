CREATE TABLE nuts_country
(
    id    VARCHAR(2) PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE nuts_region_1
(
    id              VARCHAR(3) PRIMARY KEY,
    nuts_country_id VARCHAR(2)   NOT NULL,
    title           VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nuts_region_1_nuts_country
        FOREIGN KEY (nuts_country_id) REFERENCES nuts_country (id)
            ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE nuts_region_2
(
    id               VARCHAR(4) PRIMARY KEY,
    nuts_region_1_id VARCHAR(3)   NOT NULL,
    title            VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nuts_region_2_nuts_region_1
        FOREIGN KEY (nuts_region_1_id) REFERENCES nuts_region_1 (id)
            ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE nuts_region_3
(
    id               VARCHAR(5) PRIMARY KEY,
    nuts_region_2_id VARCHAR(4)   NOT NULL,
    title            VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nuts_region_3_nuts_region_2
        FOREIGN KEY (nuts_region_2_id) REFERENCES nuts_region_2 (id)
            ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE nuts_metadata
(
    id         ENUM ('1') PRIMARY KEY,
    nuts_date  DATE         DEFAULT NULL,
    nuts_title VARCHAR(127) DEFAULT NULL
);

INSERT INTO nuts_metadata (id) VALUE (1);

CREATE TABLE programme_nuts
(
    nuts_region_3_id  VARCHAR(5) PRIMARY KEY,
    programme_data_id ENUM ('1') DEFAULT '1',
    CONSTRAINT fk_programme_nuts_nuts_region_3
        FOREIGN KEY (nuts_region_3_id) REFERENCES nuts_region_3 (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
)
