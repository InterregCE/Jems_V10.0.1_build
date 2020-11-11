CREATE TABLE project_call_flat_rate
(
    call_id       INT UNSIGNED                                                                        NOT NULL,
    type          ENUM ('StaffCost','OfficeOnStaff','OfficeOnOther', 'TravelOnStaff', 'OtherOnStaff') NOT NULL,
    rate          TINYINT UNSIGNED                                                                    NOT NULL,
    is_adjustable BOOLEAN                                                                             NOT NULL,
    CONSTRAINT pk_project_call_flat_rate PRIMARY KEY (call_id, type),
    CONSTRAINT fk_project_call_flat_rate_to_project_call FOREIGN KEY (call_id) REFERENCES project_call (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
