ALTER TABLE programme_indicator_output
    ADD COLUMN result_indicator_id INT UNSIGNED DEFAULT NULL,
    ADD CONSTRAINT fk_output_indicator_to_result_indicator
        FOREIGN KEY (result_indicator_id)
            REFERENCES programme_indicator_result (id)
            ON DELETE SET NULL;

