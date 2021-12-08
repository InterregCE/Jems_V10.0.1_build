package io.cloudflight.jems.server.user.service.model

enum class UserRolePermission(val key: String, val projectRelated: Boolean = false) {

    // Module PROGRAMME SETUP
    ProgrammeSetupRetrieve("ProgrammeSetupRetrieve"),
    ProgrammeSetupUpdate("ProgrammeSetupUpdate"),

    // Module CALL
    CallRetrieve("CallRetrieve"),
    CallPublishedRetrieve("CallPublishedRetrieve"),
    CallUpdate("CallUpdate"),

    // Module APPLICATIONS
    ProjectRetrieve("ProjectRetrieve"),
    ProjectRetrieveEditUserAssignments("ProjectRetrieveEditUserAssignments"),
    ProjectsWithOwnershipRetrieve("ProjectsWithOwnershipRetrieve"),
    ProjectCreate("ProjectCreate"),

    ProjectFormRetrieve("ProjectFormRetrieve", true),
    ProjectFormUpdate("ProjectFormUpdate", true),

    ProjectFileApplicationRetrieve("ProjectFileApplicationRetrieve", true),
    ProjectFileApplicationUpdate("ProjectFileApplicationUpdate", true),
    ProjectFileAssessmentRetrieve("ProjectFileAssessmentRetrieve", true),
    ProjectFileAssessmentUpdate("ProjectFileAssessmentUpdate", true),

    // Module APPLICATION LIFECYCLE
    ProjectCheckApplicationForm("ProjectCheckApplicationForm", true),
    ProjectSubmission("ProjectSubmission", true),

    ProjectAssessmentView("ProjectAssessmentView", true),
    ProjectAssessmentQualityEnter("ProjectAssessmentQualityEnter", true),
    ProjectAssessmentEligibilityEnter("ProjectAssessmentEligibilityEnter", true),
    ProjectStatusReturnToApplicant("ProjectStatusReturnToApplicant", true),
    ProjectStatusDecideEligible("ProjectStatusDecideEligible", true),
    ProjectStatusDecideIneligible("ProjectStatusDecideIneligible", true),
    ProjectStatusDecideApproved("ProjectStatusDecideApproved", true),
    ProjectStatusDecideApprovedWithConditions("ProjectStatusDecideApprovedWithConditions", true),
    ProjectStatusDecideNotApproved("ProjectStatusDecideNotApproved", true),
    ProjectStatusDecisionRevert("ProjectStatusDecisionRevert", true),
    ProjectStartStepTwo("ProjectStartStepTwo", true),

    ProjectModificationView("ProjectModificationView", true),
    ProjectStatusDecideModificationApproved("ProjectStatusDecideModificationApproved", true),
    ProjectStatusDecideModificationNotApproved("ProjectStatusDecideModificationNotApproved", true),
    ProjectOpenModification("ProjectOpenModification", true),
    ProjectModificationFileAssessmentRetrieve("ProjectModificationFileAssessmentRetrieve", true),
    ProjectModificationFileAssessmentUpdate("ProjectModificationFileAssessmentUpdate", true),

    ProjectCollaboratorsRetrieve("ProjectCollaboratorsRetrieve", true),
    ProjectCollaboratorsUpdate("ProjectCollaboratorsUpdate", true),

    // Module SYSTEM
    AuditRetrieve("AuditRetrieve"),

    RoleRetrieve("RoleRetrieve"),
    RoleCreate("RoleCreate"),
    RoleUpdate("RoleUpdate"),

    UserRetrieve("UserRetrieve"),
    UserCreate("UserCreate"),
    UserUpdate("UserUpdate"),
    UserUpdateRole("UserUpdateRole"),
    UserUpdatePassword("UserUpdatePassword"),
}
