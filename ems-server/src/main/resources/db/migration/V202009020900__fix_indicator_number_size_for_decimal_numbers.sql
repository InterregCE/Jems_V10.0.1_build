ALTER TABLE programme_indicator_output
    MODIFY milestone DECIMAL(11, 2) DEFAULT NULL;

ALTER TABLE programme_indicator_output
    MODIFY final_target DECIMAL(11, 2) DEFAULT NULL;

ALTER TABLE programme_indicator_result
    MODIFY baseline DECIMAL(11, 2) UNSIGNED ZEROFILL DEFAULT NULL;

ALTER TABLE programme_indicator_result
    MODIFY final_target DECIMAL(11, 2) DEFAULT NULL;
