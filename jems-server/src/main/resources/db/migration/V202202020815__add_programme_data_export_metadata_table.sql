CREATE TABLE programme_data_export_metadata
(
    plugin_key        VARCHAR(255) NOT NULL PRIMARY KEY,
    export_language   VARCHAR(3)   NOT NULL,
    input_language    VARCHAR(3)   NOT NULL,
    file_name         VARCHAR(255),
    content_type      VARCHAR(255),
    request_time      DATETIME(3)  NOT NULL,
    export_started_at DATETIME(3),
    export_ended_at   DATETIME(3)
);
