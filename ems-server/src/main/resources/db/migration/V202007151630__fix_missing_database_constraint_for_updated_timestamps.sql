ALTER TABLE project_file
    MODIFY updated DATETIME(3) NOT NULL DEFAULT NOW();

ALTER TABLE project_eligibility_assessment
    MODIFY updated DATETIME(3) NOT NULL DEFAULT NOW();

ALTER TABLE project_quality_assessment
    MODIFY updated DATETIME(3) NOT NULL DEFAULT NOW();
