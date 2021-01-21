-- move intro from project to transl table
ALTER TABLE project_transl
    ADD COLUMN intro TEXT(2000) DEFAULT NULL;

ALTER TABLE project
    DROP COLUMN intro;
ALTER TABLE project
    DROP COLUMN intro_programme_language;
