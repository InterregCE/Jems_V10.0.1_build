CREATE TABLE project_partner_contribution
(
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    partner_id INT UNSIGNED   NOT NULL,
    name       VARCHAR(255)   DEFAULT NULL,
    status     ENUM ('Private', 'Public', 'AutomaticPublic') DEFAULT NULL,
    amount     DECIMAL(11, 2) NOT NULL,
    CONSTRAINT fk_project_partner_contribution_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
