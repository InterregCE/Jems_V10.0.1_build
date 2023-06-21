DELETE FROM notification;

ALTER TABLE notification
    ADD COLUMN group_identifier    BINARY(16) NOT NULL AFTER id, # UUID
    ADD COLUMN instance_identifier BINARY(16) NOT NULL AFTER group_identifier; # UUID
