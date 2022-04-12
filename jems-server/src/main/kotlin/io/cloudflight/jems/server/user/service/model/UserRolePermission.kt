package io.cloudflight.jems.server.user.service.model

enum class UserRolePermission(val key: String, val projectRelated: Boolean = false) {

    // region Creator
    ProjectCreate("ProjectCreate"),

    ProjectCreatorCollaboratorsRetrieve("ProjectCreatorCollaboratorsRetrieve", false),
    ProjectCreatorCollaboratorsUpdate("ProjectCreatorCollaboratorsUpdate", false),
    // endregion Creator

    // region Monitor
    ProjectReportingView("ProjectReportingView", true),
    ProjectReportingEdit("ProjectReportingEdit", true),

    ProjectContractingView("ProjectContractingView", true),
    ProjectSetToContracted("ProjectSetToContracted", true),

    ProjectFormRetrieve("ProjectFormRetrieve", true),
    ProjectFormUpdate("ProjectFormUpdate", true),

    ProjectFileApplicationRetrieve("ProjectFileApplicationRetrieve", true),
    ProjectFileApplicationUpdate("ProjectFileApplicationUpdate", true),

    ProjectCheckApplicationForm("ProjectCheckApplicationForm", true),
    ProjectSubmission("ProjectSubmission", true),

    ProjectAssessmentView("ProjectAssessmentView", true),
    ProjectAssessmentQualityEnter("ProjectAssessmentQualityEnter", true),
    ProjectAssessmentEligibilityEnter("ProjectAssessmentEligibilityEnter", true),
    ProjectStatusDecideEligible("ProjectStatusDecideEligible", true),
    ProjectStatusDecideIneligible("ProjectStatusDecideIneligible", true),
    ProjectStatusDecideApproved("ProjectStatusDecideApproved", true),
    ProjectStatusDecideApprovedWithConditions("ProjectStatusDecideApprovedWithConditions", true),
    ProjectStatusDecideNotApproved("ProjectStatusDecideNotApproved", true),

    ProjectStatusDecisionRevert("ProjectStatusDecisionRevert", true),
    ProjectStatusReturnToApplicant("ProjectStatusReturnToApplicant", true),
    ProjectStartStepTwo("ProjectStartStepTwo", true),

    ProjectFileAssessmentRetrieve("ProjectFileAssessmentRetrieve", true),
    ProjectFileAssessmentUpdate("ProjectFileAssessmentUpdate", true),

    ProjectAssessmentChecklistUpdate("ProjectAssessmentChecklistUpdate", true),

    ProjectModificationView("ProjectModificationView", true),
    ProjectStatusDecideModificationApproved("ProjectStatusDecideModificationApproved", true),
    ProjectStatusDecideModificationNotApproved("ProjectStatusDecideModificationNotApproved", true),

    ProjectOpenModification("ProjectOpenModification", true),

    ProjectModificationFileAssessmentRetrieve("ProjectModificationFileAssessmentRetrieve", true),
    ProjectModificationFileAssessmentUpdate("ProjectModificationFileAssessmentUpdate", true),

    ProjectMonitorCollaboratorsRetrieve("ProjectMonitorCollaboratorsRetrieve", true),
    ProjectMonitorCollaboratorsUpdate("ProjectMonitorCollaboratorsUpdate", true),
    // endregion Monitor

    // region top navigation
    ProjectsWithOwnershipRetrieve("ProjectsWithOwnershipRetrieve"),
    CallPublishedRetrieve("CallPublishedRetrieve"),

    ProjectRetrieve("ProjectRetrieve"),
    ProjectRetrieveEditUserAssignments("ProjectRetrieveEditUserAssignments"),

    CallRetrieve("CallRetrieve"),
    CallUpdate("CallUpdate"),

    ProgrammeSetupRetrieve("ProgrammeSetupRetrieve"),
    ProgrammeSetupUpdate("ProgrammeSetupUpdate"),

    ProgrammeDataExportRetrieve("ProgrammeDataExportRetrieve"),

    UserRetrieve("UserRetrieve"),
    RoleRetrieve("RoleRetrieve"),
    UserCreate("UserCreate"),
    UserUpdate("UserUpdate"),
    UserUpdateRole("UserUpdateRole"),
    UserUpdatePassword("UserUpdatePassword"),
    RoleCreate("RoleCreate"),
    RoleUpdate("RoleUpdate"),

    AuditRetrieve("AuditRetrieve"),
    // endregion top navigation

}
