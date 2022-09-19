package io.cloudflight.jems.server.user.service.model

enum class UserRolePermission(val key: String, val projectRelated: Boolean = false) {

    // region Creator
    ProjectCreate("ProjectCreate"),

    ProjectCreatorCollaboratorsRetrieve("ProjectCreatorCollaboratorsRetrieve", false),
    ProjectCreatorCollaboratorsUpdate("ProjectCreatorCollaboratorsUpdate", false),

    ProjectCreatorContractingReportingView("ProjectCreatorContractingReportingView"),
    ProjectCreatorContractingReportingEdit("ProjectCreatorContractingReportingEdit"),
    // endregion Creator

    // region Monitor
    ProjectReportingView("ProjectReportingView", true),
    ProjectReportingEdit("ProjectReportingEdit", true),

    ProjectContractingView("ProjectContractingView", true),
    ProjectSetToContracted("ProjectSetToContracted", true),

    ProjectContractingManagementView("ProjectContractingManagementView", true),
    ProjectContractingManagementEdit("ProjectContractingManagementEdit", true),
    ProjectContractingReportingView("ProjectContractingReportingView", true),
    ProjectContractingReportingEdit("ProjectContractingReportingEdit", true),
    ProjectContractsView("ProjectContractsView", true),
    ProjectContractsEdit("ProjectContractsEdit", true),

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
    ProjectAssessmentChecklistConsolidate("ProjectAssessmentChecklistConsolidate", true),
    ProjectAssessmentChecklistSelectedRetrieve("ProjectAssessmentChecklistSelectedRetrieve", true),
    ProjectAssessmentChecklistSelectedUpdate("ProjectAssessmentChecklistSelectedUpdate", false),

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

    InstitutionsRetrieve("InstitutionsRetrieve"),
    InstitutionsUpdate("InstitutionsUpdate"),
    InstitutionsUnlimited("InstitutionsUnlimited"),

    InstitutionsAssignmentRetrieve("InstitutionsAssignmentRetrieve"),
    InstitutionsAssignmentUpdate("InstitutionsAssignmentUpdate"),

    PaymentsRetrieve("PaymentsRetrieve"),
    PaymentsUpdate("PaymentsUpdate"),

    ProgrammeDataExportRetrieve("ProgrammeDataExportRetrieve"),

    UserRetrieve("UserRetrieve"),
    RoleRetrieve("RoleRetrieve"),
    UserCreate("UserCreate"),
    UserUpdate("UserUpdate"),
    UserUpdateRole("UserUpdateRole"),
    UserUpdatePassword("UserUpdatePassword"),
    RoleCreate("RoleCreate"),
    RoleUpdate("RoleUpdate"),

    AuditRetrieve("AuditRetrieve");
    // endregion top navigation

    companion object {
        fun getGlobalProjectRetrievePermissions() = setOf(
            ProjectRetrieve,
            ProjectRetrieveEditUserAssignments,
        )

        fun getProjectMonitorPermissions() = setOf(
            ProjectFormRetrieve,
            ProjectFileApplicationRetrieve,
            ProjectCheckApplicationForm,
            ProjectAssessmentView,
            ProjectStatusDecisionRevert,
            ProjectStatusReturnToApplicant,
            ProjectStartStepTwo,
            ProjectFileAssessmentRetrieve,
            ProjectContractingView,
            ProjectSetToContracted,
            ProjectReportingView,
            ProjectReportingEdit,
            ProjectModificationView,
            ProjectOpenModification,
            ProjectModificationFileAssessmentRetrieve
        )
    }
}
