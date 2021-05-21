CREATE TABLE plugin_status
(
    plugin_key    VARCHAR(255) PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);
