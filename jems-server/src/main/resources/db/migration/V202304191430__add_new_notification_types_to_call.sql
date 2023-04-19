ALTER TABLE project_call_project_notification_configuration
    CHANGE COLUMN id id ENUM (
    'ProjectSubmittedStep1',
    'ProjectSubmitted',
    'ProjectApprovedStep1',
    'ProjectApprovedWithConditionsStep1',
    'ProjectIneligibleStep1',
    'ProjectNotApprovedStep1',
    'ProjectApproved',
    'ProjectApprovedWithConditions',
    'ProjectIneligible',
    'ProjectNotApproved',
    'ProjectReturnedToApplicant',
    'ProjectResubmitted',
    'ProjectReturnedForConditions',
    'ProjectConditionsSubmitted',
    'ProjectContracted',
    'ProjectInModification',
    'ProjectModificationSubmitted',
    'ProjectModificationApproved',
    'ProjectModificationRejected'
    ) NOT NULL;

ALTER TABLE notification
    CHANGE COLUMN type type ENUM (
    'ProjectSubmittedStep1',
    'ProjectSubmitted',
    'ProjectApprovedStep1',
    'ProjectApprovedWithConditionsStep1',
    'ProjectIneligibleStep1',
    'ProjectNotApprovedStep1',
    'ProjectApproved',
    'ProjectApprovedWithConditions',
    'ProjectIneligible',
    'ProjectNotApproved',
    'ProjectReturnedToApplicant',
    'ProjectResubmitted',
    'ProjectReturnedForConditions',
    'ProjectConditionsSubmitted',
    'ProjectContracted',
    'ProjectInModification',
    'ProjectModificationSubmitted',
    'ProjectModificationApproved',
    'ProjectModificationRejected'
    ) NOT NULL;
