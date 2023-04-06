ALTER TABLE checklist_instance
    ADD COLUMN created_at DATETIME(3) DEFAULT NULL AFTER description;
