CREATE TABLE programme_lump_sum
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    name              VARCHAR(50)                                      NOT NULL,
    description       VARCHAR(500) DEFAULT NULL,
    cost              DECIMAL(11, 2)                                   NOT NULL,
    splitting_allowed BOOLEAN                                          NOT NULL,
    phase             ENUM ('Preparation', 'Implementation','Closure') NOT NULL
);

CREATE TABLE programme_lump_sum_budget_category
(
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    programme_lump_sum_id INT UNSIGNED NOT NULL,
    category              ENUM (
        'StaffCosts',
        'OfficeAndAdministrationCosts',
        'TravelAndAccommodationCosts',
        'ExternalCosts',
        'EquipmentCosts',
        'InfrastructureCosts' )        NOT NULL,
    CONSTRAINT fk_programme_lump_sum_budget_category_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
